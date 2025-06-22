<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import { getMovieById } from '@/api/movies';
import { getReviewsForMovie, addOrUpdateRating, addReview } from '@/api/reviews';
import { useAuthStore } from '@/store/auth';
import { useMessage } from 'naive-ui';
import { NGrid, NGi, NRate, NCard, NInput, NButton, NSpace, NDivider, NList, NListItem, NThing, NAvatar, NTime, NSpin } from 'naive-ui';

const route = useRoute();
const authStore = useAuthStore();
const message = useMessage();
const movie = ref(null);
const reviews = ref([]);
const isLoading = ref(true);
const userRating = ref(0);
const newComment = ref('');

const movieId = computed(() => route.params.id);

async function fetchData() {
    isLoading.value = true;
    try {
        const [movieRes, reviewsRes] = await Promise.all([
            getMovieById(movieId.value),
            getReviewsForMovie(movieId.value)
        ]);
        movie.value = movieRes.data;
        reviews.value = reviewsRes.data;
    } catch (error) {
        message.error("获取电影详情失败");
    } finally {
        isLoading.value = false;
    }
}

async function handleRatingUpdate(score) {
    if (!authStore.isAuthenticated) {
        message.warning('请先登录再评分！');
        return;
    }
    try {
        // Naive UI rate 组件返回的是 0-5，后端需要 1-10
        await addOrUpdateRating(movieId.value, authStore.userId, score * 2);
        message.success('评分成功！');
        // 重新获取电影数据以更新平均分
        const movieRes = await getMovieById(movieId.value);
        movie.value = movieRes.data;
    } catch (error) {
        message.error('评分失败');
    }
}

async function handleReviewSubmit() {
    if (!authStore.isAuthenticated || !newComment.value.trim()) {
        return;
    }
    try {
        await addReview(movieId.value, authStore.userId, newComment.value);
        newComment.value = '';
        message.success('评论已发布！');
        // 重新获取评论列表
        const reviewsRes = await getReviewsForMovie(movieId.value);
        reviews.value = reviewsRes.data;
    } catch (error) {
        message.error('提交评论失败');
    }
}

onMounted(fetchData);
</script>

<template>
    <n-spin :show="isLoading">
        <div v-if="movie">
            <n-grid x-gap="24" y-gap="24" :cols="3">
                <n-gi :span="1">
                    <img :src="movie.posterUrl || 'https://via.placeholder.com/400x600.png?text=No+Image'"
                        :alt="movie.title" class="w-full rounded-lg shadow-lg">
                </n-gi>
                <n-gi :span="2">
                    <h1 class="text-4xl font-bold mb-2">{{ movie.title }} ({{ movie.releaseYear }})</h1>
                    <n-space class="text-gray-400 mb-4">
                        <span>{{ movie.genre }}</span> /
                        <span>{{ movie.duration }} 分钟</span> /
                        <span>{{ movie.country }}</span>
                    </n-space>

                    <n-space align="center" class="mb-6">
                        <span class="text-3xl font-bold text-yellow-400">{{ movie.averageRating?.toFixed(1) }}</span>
                        <n-rate readonly :value="movie.averageRating / 2" allow-half />
                    </n-space>

                    <p class="text-base leading-relaxed mb-6">{{ movie.synopsis }}</p>

                    <div class="mb-8">
                        <p><strong class="font-semibold">导演:</strong> {{ movie.directorNames?.join(' / ') }}</p>
                        <p><strong class="font-semibold">主演:</strong> {{ movie.actorNames?.join(' / ') }}</p>
                    </div>

                    <n-card v-if="authStore.isAuthenticated" title="为电影打分" class="mb-8"
                        style="background-color:#2a2a2a">
                        <n-rate :default-value="userRating" size="large" @update:value="handleRatingUpdate"
                            allow-half />
                    </n-card>
                </n-gi>
            </n-grid>

            <n-divider />

            <h2 class="text-2xl font-semibold mb-4">评论区</h2>
            <n-card v-if="authStore.isAuthenticated" class="mb-6" style="background-color:#2a2a2a">
                <n-input v-model:value="newComment" type="textarea" placeholder="写下你的评论..."
                    :autosize="{ minRows: 3 }" />
                <n-button @click="handleReviewSubmit" type="primary" class="mt-4">发表评论</n-button>
            </n-card>

            <n-list hoverable clickable>
                <n-list-item v-for="review in reviews" :key="review.id">
                    <n-thing>
                        <template #avatar>
                            <n-avatar>{{ review.username?.[0].toUpperCase() }}</n-avatar>
                        </template>
                        <template #header>
                            {{ review.username }}
                        </template>
                        <template #header-extra>
                            <n-time :time="new Date(review.createdAt)" type="relative" />
                        </template>
                        {{ review.commentText }}
                    </n-thing>
                </n-list-item>
            </n-list>
        </div>
    </n-spin>
</template>