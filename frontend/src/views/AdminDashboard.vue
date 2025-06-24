<template>
    <n-layout-content>
        <n-h1>管理后台</n-h1>
        <n-tabs type="line" animated @update:value="handleTabChange">

            <!-- ==================== 1. 影视管理 (重构后) ==================== -->
            <n-tab-pane name="cinema" tab="影视管理">
                <n-space vertical>
                    <n-flex justify="space-between">
                        <!-- 左侧：表格切换 和 搜索框 -->
                        <n-space align="center">
                            <n-radio-group v-model:value="currentTable" name="table-switch">
                                <n-radio-button value="movies">电影</n-radio-button>
                                <n-radio-button value="actors">演员</n-radio-button>
                                <n-radio-button value="directors">导演</n-radio-button>
                            </n-radio-group>

                            <n-input v-if="currentTable === 'movies'" v-model:value="movieSearchKeyword"
                                placeholder="按电影名称搜索..." clearable style="width: 240px;"
                                @update:value="filterCinemaData">
                                <template #prefix>
                                    <n-icon :component="SearchIcon" />
                                </template>
                            </n-input>
                            <n-input v-if="currentTable === 'actors'" v-model:value="actorSearchKeyword"
                                placeholder="按演员姓名搜索..." clearable style="width: 240px;"
                                @update:value="filterCinemaData">
                                <template #prefix>
                                    <n-icon :component="SearchIcon" />
                                </template>
                            </n-input>
                            <n-input v-if="currentTable === 'directors'" v-model:value="directorSearchKeyword"
                                placeholder="按导演姓名搜索..." clearable style="width: 240px;"
                                @update:value="filterCinemaData">
                                <template #prefix>
                                    <n-icon :component="SearchIcon" />
                                </template>
                            </n-input>
                        </n-space>

                        <!-- 右侧：新增按钮 -->
                        <n-button type="primary"
                            @click="handleOpenModal(currentTable === 'movies' ? 'movie' : (currentTable === 'actors' ? 'actor' : 'director'), 'create')">
                            新增{{ { movies: '电影', actors: '演员', directors: '导演' }[currentTable] }}
                        </n-button>
                    </n-flex>

                    <!-- 电影表格 -->
                    <n-data-table v-if="currentTable === 'movies'" :columns="movieColumns"
                        :data="filteredCinemaData.movies" :loading="loadingCinema" :pagination="{ pageSize: 10 }" />
                    <!-- 演员表格 -->
                    <n-data-table v-if="currentTable === 'actors'" :columns="personColumns('actor')"
                        :data="filteredCinemaData.actors" :loading="loadingCinema" :pagination="{ pageSize: 10 }" />
                    <!-- 导演表格 -->
                    <n-data-table v-if="currentTable === 'directors'" :columns="personColumns('director')"
                        :data="filteredCinemaData.directors" :loading="loadingCinema" :pagination="{ pageSize: 10 }" />
                </n-space>
            </n-tab-pane>

            <!-- ==================== 2. 影评管理 ==================== -->
            <n-tab-pane name="reviews" tab="影评管理">
                <n-space align="center" style="margin-bottom: 16px;">
                    <n-input v-model:value="reviewFilterKeyword" placeholder="请输入电影名称筛选" clearable style="width: 300px;"
                        @update:value="filterReviews">
                        <template #prefix>
                            <n-icon :component="SearchIcon" />
                        </template>
                    </n-input>
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
                        <n-input v-model:value="userSearchKeyword" placeholder="输入用户名进行搜索" @keyup.enter="searchUsers">
                            <template #prefix>
                                <n-icon :component="SearchIcon" />
                            </template>
                        </n-input>
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
import { ref, h, onMounted } from 'vue';
import apiService from '@/services/apiService';
import {
    NLayoutContent, NTabs, NTabPane, NH1, NDataTable, NButton, NSpace, NPopconfirm, useMessage,
    NModal, NFlex, NInput, NRate, NH2, NP, NInputGroup, NSpin, NEmpty, NText,
    NRadioGroup, NRadioButton, NIcon
} from 'naive-ui';
import MovieForm from '@/components/forms/MovieForm.vue';
import PersonForm from '@/components/forms/PersonForm.vue';
import { SearchOutline as SearchIcon } from '@vicons/ionicons5';

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
        formData.value = {
            ...item,
            actorIds: item.actorIds,
            directorIds: item.directorIds
        };
    } else {
        formData.value = { ...item };
    }
    modalTitle.value = `${mode === 'create' ? '新增' : '编辑'} ${{ movie: '电影', actor: '演员', director: '导演' }[type]}`;
    showModal.value = true;
};

const handleSubmit = async () => {
    try {
        // 现在可以直接 await，因为子组件返回了 Promise
        await formRef.value.validate();

        // --- 验证成功 ---
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
        } catch (apiError) {
            message.error('操作失败: ' + (apiError.response?.data?.message || apiError.message));
        } finally {
            isSubmitting.value = false;
        }

    } catch (validationErrors) {
        // --- 验证失败 ---
        console.log('表单验证失败', validationErrors);
        message.warning('请检查表单，所有必填项都需要填写。');
    }
};

// ==================== 影视管理逻辑 ====================
const loadingCinema = ref(true);
const cinemaData = ref({ movies: [], actors: [], directors: [] });
const filteredCinemaData = ref({ movies: [], actors: [], directors: [] });
const currentTable = ref('movies');
const movieSearchKeyword = ref('');
const actorSearchKeyword = ref('');
const directorSearchKeyword = ref('');

const createMovieColumns = () => [
    { title: 'ID', key: 'id', width: 60 },
    {
        title: '海报',
        key: 'posterUrl',
        width: 160,
        render(row) {
            return h('img', {
                src: row.posterUrl,
                style: {
                    width: '108px',
                    height: '160px',
                    objectFit: 'cover',
                    borderRadius: '3px'
                }
            });
        }
    },
    { title: '名称', key: 'title', ellipsis: { tooltip: true } },
    { title: '年份', key: 'releaseYear', width: 80 },
    {
        title: '操作', key: 'actions', render: (row) => h(NSpace, null, {
            default: () => [
                h(NButton, { size: 'small', onClick: () => handleOpenModal('movie', 'edit', row) }, { default: () => '编辑' }),
                h(NPopconfirm, { onPositiveClick: () => handleDeleteCinema('movie', row.id) }, {
                    trigger: () => h(NButton, { size: 'small', type: 'error', ghost: true }, { default: () => '删除' }),
                    default: () => '确定要删除吗？'
                })
            ]
        })
    }
];

const createPersonColumns = (type) => [
    { title: 'ID', key: 'id', width: 60 },
    {
        title: '头像',
        key: 'profileImageUrl',
        width: 160,
        render(row) {
            return h('img', {
                src: row.profileImageUrl,
                style: {
                    width: '108px',
                    height: '160px',
                    objectFit: 'cover',
                    borderRadius: '3px'
                }
            });
        }
    },
    { title: '姓名', key: 'name', ellipsis: { tooltip: true } },
    { title: '国籍', key: 'nationality' },
    {
        title: '操作', key: 'actions', render: (row) => h(NSpace, null, {
            default: () => [
                h(NButton, { size: 'small', onClick: () => handleOpenModal(type, 'edit', row) }, { default: () => '编辑' }),
                h(NPopconfirm, { onPositiveClick: () => handleDeleteCinema(type, row.id) }, {
                    trigger: () => h(NButton, { size: 'small', type: 'error', ghost: true }, { default: () => '删除' }),
                    default: () => '确定要删除吗？'
                })
            ]
        })
    }
];

const movieColumns = createMovieColumns();
const personColumns = (type) => createPersonColumns(type);

const fetchCinemaData = async () => {
    loadingCinema.value = true;
    try {
        const [moviesRes, actorsRes, directorsRes] = await Promise.all([
            apiService.getAllMovies(),
            apiService.getAllActors(),
            apiService.getAllDirectors()
        ]);
        cinemaData.value.movies = moviesRes.data.content;
        cinemaData.value.actors = actorsRes.data;
        cinemaData.value.directors = directorsRes.data;
        filterCinemaData();
    } catch (error) {
        message.error('加载影视数据失败');
    } finally {
        loadingCinema.value = false;
    }
};

const filterCinemaData = () => {
    const movieKeyword = movieSearchKeyword.value.toLowerCase().trim();
    filteredCinemaData.value.movies = movieKeyword
        ? cinemaData.value.movies.filter(m => m.title.toLowerCase().includes(movieKeyword))
        : cinemaData.value.movies;

    const actorKeyword = actorSearchKeyword.value.toLowerCase().trim();
    filteredCinemaData.value.actors = actorKeyword
        ? cinemaData.value.actors.filter(a => a.name.toLowerCase().includes(actorKeyword))
        : cinemaData.value.actors;

    const directorKeyword = directorSearchKeyword.value.toLowerCase().trim();
    filteredCinemaData.value.directors = directorKeyword
        ? cinemaData.value.directors.filter(d => d.name.toLowerCase().includes(directorKeyword))
        : cinemaData.value.directors;
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
        if (type === 'movie') cinemaData.value.movies = cinemaData.value.movies.filter(item => item.id !== id);
        else if (type === 'actor') cinemaData.value.actors = cinemaData.value.actors.filter(item => item.id !== id);
        else if (type === 'director') cinemaData.value.directors = cinemaData.value.directors.filter(item => item.id !== id);
        filterCinemaData();
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
    { title: '评分', key: 'score', render: (row) => row.score ? h(NRate, { readonly: true, value: row.score / 2, size: 'small', allowHalf: true }) : h(NText, { depth: 3 }, { default: () => '未评分' }) },
    { title: '评论内容', key: 'commentText', ellipsis: { tooltip: true } },
    { title: '用户名', key: 'username' },
    { title: '点赞数', key: 'likes', width: 80, render: (row) => h(NText, { type: row.likes > 0 ? 'success' : row.likes < 0 ? 'error' : 'default' }, { default: () => row.likes }) },
    { title: '评论时间', key: 'createdAt', render: (row) => new Date(row.createdAt).toLocaleString() },
    {
        title: '操作', key: 'actions', render: (row) => h(NPopconfirm, { onPositiveClick: () => deleteReview(row.id) }, {
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
    if (value === 'cinema' && cinemaData.value.movies.length === 0) {
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