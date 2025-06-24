<template>
    <n-layout-content>
        <n-h1>管理后台</n-h1>
        <n-tabs type="line" animated>
            <n-tab-pane name="movies" tab="影视管理">
                <n-h2>电影/演员/导演管理</n-h2>
                <n-p>此区域用于增、删、改电影、演员和导演信息。</n-p>
                <n-p depth="3">（功能待实现：可在此处放置表格和表单来进行CRUD操作）</n-p>
            </n-tab-pane>

            <n-tab-pane name="reviews" tab="影评管理">
                <n-h2>影评管理</n-h2>
                <n-p>在此处可以搜索电影，并删除其下的任何影评。</n-p>
                <n-space vertical>
                    <n-input-group>
                        <n-input v-model:value="movieSearchKeyword" placeholder="输入电影名称搜索影评"
                            @keyup.enter="searchReviews" />
                        <n-button type="primary" @click="searchReviews" :loading="searchingMovies">搜索</n-button>
                    </n-input-group>

                    <n-list v-if="searchedMovies.length > 0" bordered>
                        <n-list-item v-for="movie in searchedMovies" :key="movie.id">
                            <n-button text @click="loadReviewsForMovie(movie)">{{ movie.title }} ({{ movie.releaseYear
                                }})</n-button>
                        </n-list-item>
                    </n-list>

                    <n-divider v-if="selectedMovie" />

                    <div v-if="selectedMovie">
                        <n-h3>《{{ selectedMovie.title }}》的影评列表</n-h3>
                        <n-spin :show="loadingReviews">
                            <n-list bordered separator>
                                <n-list-item v-for="review in reviews" :key="review.id">
                                    <n-thing :title="review.username" :description="review.commentText">
                                        <template #header-extra>
                                            <n-popconfirm @positive-click="deleteReview(review.id)">
                                                <template #trigger>
                                                    <n-button size="small" type="error" ghost>删除</n-button>
                                                </template>
                                                确定要删除这条来自用户 "{{ review.username }}" 的评论吗？
                                            </n-popconfirm>
                                        </template>
                                        <template #description>
                                            <n-text depth="3">{{ new Date(review.createdAt).toLocaleString() }}</n-text>
                                        </template>
                                    </n-thing>
                                </n-list-item>
                                <n-list-item v-if="reviews.length === 0">
                                    <n-empty description="该电影暂无影评。" />
                                </n-list-item>
                            </n-list>
                        </n-spin>
                    </div>
                </n-space>
            </n-tab-pane>

            <n-tab-pane name="users" tab="用户管理">
                <n-h2>用户管理</n-h2>
                <n-p>此区域用于删除用户账号。</n-p>
                <n-p depth="3">（功能待实现：可在此处放置用户列表，并提供删除按钮）</n-p>
            </n-tab-pane>
        </n-tabs>
    </n-layout-content>
</template>

<script setup>
import { ref } from 'vue';
import apiService from '@/services/apiService';
import {
    NLayoutContent, NTabs, NTabPane, NH1, NH2, NH3, NP, NSpace, NInputGroup,
    NInput, NButton, NList, NListItem, NSpin, NThing, NPopconfirm, NText, NEmpty, NDivider, useMessage
} from 'naive-ui';

const message = useMessage();

// 影评管理相关
const movieSearchKeyword = ref('');
const searchingMovies = ref(false);
const searchedMovies = ref([]);
const selectedMovie = ref(null);
const loadingReviews = ref(false);
const reviews = ref([]);

const searchReviews = async () => {
    if (!movieSearchKeyword.value.trim()) {
        message.warning('请输入电影名称');
        return;
    }
    searchingMovies.value = true;
    selectedMovie.value = null;
    reviews.value = [];
    try {
        // 使用 MovieSearch 相同的逻辑来搜索电影
        const response = await apiService.searchMovies({ type: 'by-actor', name: movieSearchKeyword.value });
        const response2 = await apiService.searchMovies({ type: 'by-director', name: movieSearchKeyword.value });
        const allMovies = [...response.data, ...response2.data];
        searchedMovies.value = Array.from(new Map(allMovies.map(m => [m.id, m])).values());

        if (searchedMovies.value.length === 0) {
            message.info('未找到相关电影');
        }
    } catch (error) {
        message.error('搜索电影失败');
    } finally {
        searchingMovies.value = false;
    }
};

const loadReviewsForMovie = async (movie) => {
    selectedMovie.value = movie;
    loadingReviews.value = true;
    try {
        const response = await apiService.getReviewsForMovie(movie.id);
        reviews.value = response.data;
    } catch (error) {
        message.error('加载影评失败');
    } finally {
        loadingReviews.value = false;
    }
};

const deleteReview = async (reviewId) => {
    try {
        await apiService.deleteReview(reviewId);
        message.success('影评删除成功');
        // 重新加载评论列表
        if (selectedMovie.value) {
            loadReviewsForMovie(selectedMovie.value);
        }
    } catch (error) {
        message.error('删除失败');
    }
};
</script>