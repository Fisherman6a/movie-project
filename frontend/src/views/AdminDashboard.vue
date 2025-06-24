<template>
    <n-layout-content>
        <n-h1>管理后台</n-h1>
        <n-tabs type="line" animated @update:value="handleTabChange">

            <!-- ==================== 1. 影视管理 ==================== -->
            <n-tab-pane name="cinema" tab="影视管理">
                <n-space justify="end" style="margin-bottom: 16px;">
                    <n-button type="primary" @click="handleOpenModal('movie', 'create')">新增电影</n-button>
                    <n-button type="primary" @click="handleOpenModal('actor', 'create')">新增演员</n-button>
                    <n-button type="primary" @click="handleOpenModal('director', 'create')">新增导演</n-button>
                </n-space>
                <n-data-table :columns="cinemaColumns" :data="cinemaData" :loading="loadingCinema"
                    :pagination="cinemaPagination" />
            </n-tab-pane>

            <!-- ==================== 2. 影评管理 ==================== -->
            <n-tab-pane name="reviews" tab="影评管理">
                <n-space align="center" style="margin-bottom: 16px;">
                    <n-input v-model:value="reviewFilterKeyword" placeholder="请输入电影名称筛选" clearable style="width: 300px;"
                        @update:value="filterReviews" />
                    <n-button @click="resetReviewFilter">重置</n-button>
                </n-space>
                <n-data-table :columns="reviewColumns" :data="filteredReviews" :loading="loadingReviews"
                    :pagination="{ pageSize: 10 }" />
            </n-tab-pane>

            <!-- ==================== 3. 用户管理 ==================== -->
            <n-tab-pane name="users" tab="用户管理">
                <n-h2>用户管理</n-h2>
                <n-p>通过用户名搜索用户并进行管理。</n-p>
                <n-space vertical>
                    <n-input-group>
                        <n-input v-model:value="userSearchKeyword" placeholder="输入用户名进行搜索" @keyup.enter="searchUsers" />
                        <n-button type="primary" @click="searchUsers" :loading="loadingUsers">搜索</n-button>
                    </n-input-group>
                    <n-spin :show="loadingUsers">
                        <n-data-table v-if="searchedUsers.length > 0" :columns="userColumns" :data="searchedUsers"
                            :pagination="{ pageSize: 10 }" />
                        <n-empty v-else description="输入用户名进行搜索，或无匹配结果。" style="margin-top: 24px;" />
                    </n-spin>
                </n-space>
            </n-tab-pane>

        </n-tabs>

        <!-- ==================== 通用模态框 ==================== -->
        <n-modal v-model:show="showModal" preset="card" :title="modalTitle" style="width: 600px;">
            <MovieForm v-if="modalEntityType === 'movie'" ref="formRef" v-model="formData" />
            <PersonForm v-else ref="formRef" v-model="formData" />
            <template #footer>
                <n-flex justify="end">
                    <n-button @click="showModal = false">取消</n-button>
                    <n-button type="primary" @click="handleSubmit" :loading="isSubmitting">提交</n-button>
                </n-flex>
            </template>
        </n-modal>
    </n-layout-content>
</template>

<script setup>
import { ref, h, onMounted } from 'vue'; // 删除了未使用的 'computed'
import apiService from '@/services/apiService';
import {
    NLayoutContent, NTabs, NTabPane, NH1, NDataTable, NButton, NSpace, NPopconfirm, useMessage,
    NModal, NFlex, NInput, NRate, NH2, NP, NInputGroup, NSpin, NEmpty, NText
} from 'naive-ui';
import MovieForm from '@/components/forms/MovieForm.vue';
import PersonForm from '@/components/forms/PersonForm.vue';

const message = useMessage();
const formRef = ref(null);

// ==================== 通用模态框逻辑 ====================
const showModal = ref(false);
const modalMode = ref('create');
const modalEntityType = ref('');
const modalTitle = ref('');
const formData = ref({});
const isSubmitting = ref(false);

const handleOpenModal = (type, mode, item = {}) => {
    modalEntityType.value = type;
    modalMode.value = mode;
    if (type === 'movie' && mode === 'edit') {
        // 后端返回的 MovieDTO 中 cast 和 directors 是对象数组，需要提取 id
        formData.value = {
            ...item,
            actorIds: item.cast.map(a => a.id),
            directorIds: item.directors.map(d => d.id)
        };
    } else {
        formData.value = { ...item };
    }
    modalTitle.value = `${mode === 'create' ? '新增' : '编辑'} ${{ movie: '电影', actor: '演员', director: '导演' }[type]}`;
    showModal.value = true;
};

const handleSubmit = () => {
    formRef.value.validate(async (errors) => {
        if (!errors) {
            isSubmitting.value = true;
            try {
                const type = modalEntityType.value;
                const mode = modalMode.value;
                const apiMap = {
                    movie: { create: apiService.createMovie, update: apiService.updateMovie },
                    actor: { create: apiService.createActor, update: apiService.updateActor },
                    director: { create: apiService.createDirector, update: apiService.updateDirector },
                };

                if (mode === 'create') {
                    await apiMap[type].create(formData.value);
                } else {
                    await apiMap[type].update(formData.value.id, formData.value);
                }

                message.success('操作成功');
                showModal.value = false;
                fetchCinemaData();
            } catch (error) {
                message.error('操作失败: ' + (error.response?.data?.message || error.message));
            } finally {
                isSubmitting.value = false;
            }
        }
    });
};

// ==================== 影视管理逻辑 ====================
const loadingCinema = ref(true);
const cinemaData = ref([]);
const cinemaPagination = { pageSize: 10 }; // 改为每页10条，更统一

const createCinemaColumns = () => [
    { title: 'ID', key: 'id', width: 60 },
    { title: '名称', key: 'name' },
    { title: '类型', key: 'type', width: 80 },
    {
        title: '操作',
        key: 'actions',
        render(row) {
            return h(NSpace, null, {
                default: () => [
                    h(NButton, { size: 'small', onClick: () => handleOpenModal(row.entityType, 'edit', row.original) }, { default: () => '编辑' }),
                    h(NPopconfirm, { onPositiveClick: () => handleDeleteCinema(row.entityType, row.id) }, {
                        trigger: () => h(NButton, { size: 'small', type: 'error', ghost: true }, { default: () => '删除' }),
                        default: () => '确定要删除吗？'
                    })
                ]
            });
        }
    }
];
const cinemaColumns = createCinemaColumns();

const fetchCinemaData = async () => {
    loadingCinema.value = true;
    try {
        const [moviesRes, actorsRes, directorsRes] = await Promise.all([
            apiService.getAllMovies(),
            apiService.getAllActors(),
            apiService.getAllDirectors()
        ]);
        const movies = moviesRes.data.content.map(m => ({ id: m.id, type: '电影', name: m.title, entityType: 'movie', original: m }));
        const actors = actorsRes.data.map(a => ({ id: a.id, type: '演员', name: a.name, entityType: 'actor', original: a }));
        const directors = directorsRes.data.map(d => ({ id: d.id, type: '导演', name: d.name, entityType: 'director', original: d }));
        cinemaData.value = [...movies, ...actors, ...directors].sort((a, b) => b.id - a.id); // 按ID降序
    } catch (error) {
        message.error('加载影视数据失败');
    } finally {
        loadingCinema.value = false;
    }
};

const handleDeleteCinema = async (type, id) => {
    const apiMap = {
        movie: apiService.deleteMovie,
        actor: apiService.deleteActor,
        director: apiService.deleteDirector
    };
    try {
        await apiMap[type](id);
        message.success('删除成功');
        fetchCinemaData();
    } catch (error) {
        message.error('删除失败');
    }
};

// ==================== 影评管理逻辑 ====================
const loadingReviews = ref(false);
const allReviews = ref([]);
const filteredReviews = ref([]);
const reviewFilterKeyword = ref('');

const createReviewColumns = () => [
    { title: '电影名称', key: 'movieTitle', ellipsis: { tooltip: true } },
    {
        title: '评分',
        key: 'score',
        render(row) {
            if (row.score) {
                return h(NRate, { readonly: true, value: row.score / 2, size: 'small', allowHalf: true });
            }
            return h(NText, { depth: 3 }, { default: () => '未评分' });
        }
    },
    { title: '评论内容', key: 'commentText', ellipsis: { tooltip: true } },
    { title: '用户名', key: 'username' },
    {
        title: '评论时间',
        key: 'createdAt',
        render: (row) => new Date(row.createdAt).toLocaleString()
    },
    {
        title: '操作',
        key: 'actions',
        render: (row) => h(NPopconfirm, { onPositiveClick: () => deleteReview(row.id) }, {
            trigger: () => h(NButton, { size: 'small', type: 'error', ghost: true }, { default: () => '删除' }),
            default: () => `确定要删除这条评论吗？`
        })
    }
];
const reviewColumns = createReviewColumns();

const fetchAllReviews = async () => {
    loadingReviews.value = true;
    try {
        const response = await apiService.getAllReviews();
        allReviews.value = response.data;
        filteredReviews.value = allReviews.value;
    } catch (error) {
        message.error('加载所有评论失败');
    } finally {
        loadingReviews.value = false;
    }
};

const filterReviews = () => {
    const keyword = reviewFilterKeyword.value ? reviewFilterKeyword.value.toLowerCase().trim() : '';
    if (!keyword) {
        filteredReviews.value = allReviews.value;
        return;
    }
    filteredReviews.value = allReviews.value.filter(review =>
        review.movieTitle.toLowerCase().includes(keyword)
    );
};

const resetReviewFilter = () => {
    reviewFilterKeyword.value = '';
    filteredReviews.value = allReviews.value;
};

const deleteReview = async (reviewId) => {
    try {
        await apiService.deleteReview(reviewId);
        message.success('影评删除成功');
        allReviews.value = allReviews.value.filter(r => r.id !== reviewId);
        filterReviews();
    } catch (error) {
        message.error('删除失败');
    }
};

// ==================== 用户管理逻辑 ====================
const userSearchKeyword = ref('');
const loadingUsers = ref(false);
const allUsers = ref([]);
const searchedUsers = ref([]);

const fetchAllUsers = async () => {
    loadingUsers.value = true;
    try {
        const response = await apiService.getAllUsers();
        allUsers.value = response.data;
        searchedUsers.value = allUsers.value;
    } catch (error) {
        message.error('加载用户列表失败');
    } finally {
        loadingUsers.value = false;
    }
};

const searchUsers = () => {
    const keyword = userSearchKeyword.value ? userSearchKeyword.value.toLowerCase().trim() : '';
    if (!keyword) {
        searchedUsers.value = allUsers.value;
        return;
    }
    searchedUsers.value = allUsers.value.filter(user =>
        user.username.toLowerCase().includes(keyword)
    );
};

const createUserColumns = () => [
    { title: 'ID', key: 'id' },
    { title: '用户名', key: 'username' },
    { title: '邮箱', key: 'email' },
    { title: '角色', key: 'role' },
    {
        title: '操作',
        key: 'actions',
        render: (row) => h(NPopconfirm, { onPositiveClick: () => handleDeleteUser(row.id) }, {
            trigger: () => h(NButton, { size: 'small', type: 'error', ghost: true, disabled: row.role === 'ROLE_ADMIN' }, { default: () => '删除' }),
            default: () => `确定要删除用户 "${row.username}" 吗？`
        })
    }
];
const userColumns = createUserColumns();

const handleDeleteUser = async (id) => {
    try {
        await apiService.deleteUser(id);
        message.success('用户删除成功');
        allUsers.value = allUsers.value.filter(u => u.id !== id);
        searchUsers();
    } catch (error) {
        message.error('删除失败: ' + (error.response?.data?.message || error.message));
    }
};

// ==================== Tab 切换逻辑 ====================
const handleTabChange = (value) => {
    if (value === 'cinema' && cinemaData.value.length === 0) {
        fetchCinemaData();
    } else if (value === 'reviews' && allReviews.value.length === 0) {
        fetchAllReviews();
    } else if (value === 'users' && allUsers.value.length === 0) {
        fetchAllUsers();
    }
};

// 初始加载第一个 Tab 的数据
onMounted(() => {
    fetchCinemaData();
});
</script>