<template>
  <n-layout-content>
    <n-spin :show="loading">
      <div v-if="movie">
        <!-- 1. 电影标题 -->
        <n-h1>
          <n-text style="font-weight: bold;">{{ movie.title }}</n-text>
          <n-text :depth="3" style="font-size: 24px; margin-left: 12px;">({{ movie.releaseYear }})</n-text>
        </n-h1>

        <!-- 2. 主体内容区 -->
        <n-grid :x-gap="24" :cols="10">
          <!-- 2.1 左侧海报 -->
          <n-gi :span="3">
            <img :src="movie.posterUrl" class="poster-img" :alt="movie.title" />
          </n-gi>

          <!-- 2.2 中间详细信息 -->
          <n-gi :span="5">
            <n-space vertical size="small" class="movie-meta">
              <p><n-text class="meta-key">导演:</n-text>
                <router-link v-for="(director, index) in movie.directors" :key="director.id"
                  :to="{ name: 'PersonDetail', params: { id: director.id } }" class="person-link">
                  {{ director.name }}<span v-if="index < movie.directors.length - 1"> / </span>
                </router-link>
              </p>
              <p><n-text class="meta-key">主演:</n-text>
                <router-link v-for="(actor, index) in movie.cast" :key="actor.id"
                  :to="{ name: 'PersonDetail', params: { id: actor.id } }" class="person-link">
                  {{ actor.name }}<span v-if="index < movie.cast.length - 1"> / </span>
                </router-link>
              </p>
              <p><n-text class="meta-key">类型:</n-text> {{ movie.genre }}</p>
              <p><n-text class="meta-key">国家/地区:</n-text> {{ movie.country }}</p>
              <p><n-text class="meta-key">语言:</n-text> {{ movie.language }}</p>
              <p><n-text class="meta-key">片长:</n-text> {{ movie.duration }} 分钟</p>
            </n-space>
          </n-gi>

          <!-- 2.3 右侧评分卡 -->
          <n-gi :span="2">
            <n-card size="small" class="rating-box">
              <n-text depth="3">项目评分</n-text>
              <n-flex align="baseline" justify="center">
                <n-text style="font-size: 32px; font-weight: bold;">
                  {{ typeof movie.averageRating === 'number' ? movie.averageRating.toFixed(1) : '暂无' }}
                </n-text>
              </n-flex>
              <n-rate readonly :value="(movie.averageRating || 0) / 2" allow-half />

              <div class="rating-distribution">
                <div v-for="star in 5" :key="star" class="star-row">
                  <n-text>{{ 6 - star }} 星</n-text>
                  <n-progress type="line" :percentage="getStarPercentage(6 - star)" :show-indicator="false"
                    color="#f2c038" rail-color="rgba(255, 255, 255, 0.12)" />
                  <n-text depth="3" class="percentage-text">{{ getStarPercentage(6 - star).toFixed(1) }}%</n-text>
                </div>
              </div>
            </n-card>
          </n-gi>
        </n-grid>

        <!-- 3. 剧情简介与评价区 -->
        <n-grid :x-gap="24" :cols="10" style="margin-top: 24px;">
          <n-gi :span="7">
            <n-h2>剧情简介</n-h2>
            <n-p class="synopsis-text">{{ movie.synopsis }}</n-p>

            <n-h2>演职员表</n-h2>
            <n-scrollbar x-scrollable>
              <n-space :size="24" class="cast-list">
                <div v-for="person in movie.directors" :key="`dir-${person.id}`" class="person-card">
                  <router-link :to="{ name: 'PersonDetail', params: { id: person.id } }">
                    <n-avatar :size="80" :src="person.profileImageUrl" />
                    <p class="person-name">{{ person.name }}</p>
                    <p class="person-role">导演</p>
                  </router-link>
                </div>
                <div v-for="person in movie.cast" :key="`act-${person.id}`" class="person-card">
                  <router-link :to="{ name: 'PersonDetail', params: { id: person.id } }">
                    <n-avatar :size="80" :src="person.profileImageUrl" />
                    <p class="person-name">{{ person.name }}</p>
                    <p class="person-role">演员</p>
                  </router-link>
                </div>
              </n-space>
            </n-scrollbar>
          </n-gi>
          <n-gi :span="3">
            <n-h2>评价</n-h2>
            <div v-if="authStore.isAuthenticated" class="review-input-section">
              <n-rate v-model:value="myRating" size="large" />
              <n-text v-if="myRating > 0" depth="3">{{ ratingText[myRating - 1] }}</n-text>
              <n-input v-model:value="myComment" type="textarea" placeholder="写下你的短评..."
                :autosize="{ minRows: 4, maxRows: 8 }" style="margin-top: 12px;" />
              <n-button type="primary" @click="submitReview" :loading="isSubmitting"
                style="margin-top: 12px; width: 100%;">
                提交
              </n-button>
            </div>
            <n-alert v-else title="提示" type="info" :bordered="false">
              请<router-link to="/login" class="login-link">登录</router-link>后发表评论。
            </n-alert>
          </n-gi>
        </n-grid>

        <!-- 4. 影评列表 -->
        <n-h2 style="margin-top: 24px;">热门影评</n-h2>
        <n-list bordered separator style="background-color: #1a1a1f;">
          <n-list-item v-for="review in reviews" :key="review.id" class="review-item">
            <n-thing>
              <template #avatar>
                <n-avatar round size="medium">{{ review.username.charAt(0) }}</n-avatar>
              </template>
              <template #header>
                <n-text strong>{{ review.username }}</n-text>
              </template>
              <template #header-extra>
                <n-text depth="3">{{ new Date(review.createdAt).toLocaleDateString() }}</n-text>
              </template>
              <p>{{ review.commentText }}</p>
              <template #footer>
                <n-space align="center">
                  <n-button text @click="handleVote(review, 'up')">
                    <template #icon><n-icon :component="ThumbsUpIcon" /></template>
                  </n-button>
                  <n-text :type="review.likes > 0 ? 'success' : review.likes < 0 ? 'error' : 'default'"
                    style="font-weight: bold;">
                    {{ review.likes }}
                  </n-text>
                  <n-button text @click="handleVote(review, 'down')">
                    <template #icon><n-icon :component="ThumbsDownIcon" /></template>
                  </n-button>
                </n-space>
              </template>
            </n-thing>
          </n-list-item>
          <n-list-item v-if="reviews.length === 0">
            <n-empty description="暂无影评，期待你的第一篇。" />
          </n-list-item>
        </n-list>
      </div>
      <n-empty v-else description="电影信息加载失败或不存在。" />
    </n-spin>
  </n-layout-content>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import apiService from '@/services/apiService';
import { useAuthStore } from '@/stores/authStore';
import { ThumbsUpOutline as ThumbsUpIcon, ThumbsDownOutline as ThumbsDownIcon } from '@vicons/ionicons5';
import {
  NLayoutContent, NSpin, NGrid, NGi, NH1, NH2, NP, NText, NRate, NInput, NButton, NList, NListItem,
  NThing, NEmpty, NFlex, NAlert, useMessage, NCard, NSpace, NAvatar, NIcon, NProgress, NScrollbar
} from "naive-ui";

const props = defineProps({
  id: String,
});

const message = useMessage();
const authStore = useAuthStore();

const movie = ref(null);
const reviews = ref([]);
const loading = ref(true);
const isSubmitting = ref(false);

const myRating = ref(0);
const myComment = ref("");
const ratingText = ["很差", "较差", "还行", "推荐", "力荐"];

const getStarPercentage = (star) => {
  if (!movie.value || !movie.value.ratingDistribution) return 0;
  const found = movie.value.ratingDistribution.find(d => d.star === star);
  return found ? found.percentage : 0;
};

const fetchMovieData = async () => {
  loading.value = true;
  try {
    const movieResponse = await apiService.getMovieById(props.id);
    movie.value = movieResponse.data;
    const reviewsResponse = await apiService.getReviewsForMovie(props.id);
    reviews.value = reviewsResponse.data.sort(
      (a, b) => (b.likes - a.likes) || (new Date(b.createdAt) - new Date(a.createdAt))
    );
  } catch (error) {
    message.error("加载电影数据失败");
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const submitReview = async () => {
  if (myRating.value === 0 && !myComment.value.trim()) {
    message.warning("请至少给出评分或评论内容");
    return;
  }
  isSubmitting.value = true;
  try {
    if (myRating.value > 0) {
      await apiService.rateMovie(
        props.id,
        authStore.userId,
        myRating.value * 2
      );
    }
    if (myComment.value.trim()) {
      await apiService.addReview(
        props.id,
        authStore.userId,
        myComment.value.trim()
      );
    }
    message.success("评价成功！");
    myComment.value = "";
    myRating.value = 0;
    await fetchMovieData();
  } catch (error) {
    message.error("提交失败，可能您已评价过。");
    console.error(error);
  } finally {
    isSubmitting.value = false;
  }
};

const handleVote = async (review, direction) => {
  if (!authStore.isAuthenticated) {
    message.warning('请先登录再进行投票');
    return;
  }
  try {
    const response = await apiService.voteOnReview(review.id, direction);
    review.likes = response.data.likes;
  } catch (error) {
    message.error('投票失败');
    console.error(error);
  }
};

onMounted(fetchMovieData);
</script>

<style scoped>
.poster-img {
  width: 100%;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.movie-meta {
  font-size: 14px;
}

.meta-key {
  color: rgba(255, 255, 255, 0.52);
  margin-right: 8px;
}

.movie-meta p {
  margin: 4px 0;
}

.rating-box {
  text-align: center;
  background-color: #1a1a1f;
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.synopsis-text {
  line-height: 1.7;
  text-indent: 2em;
}

.review-input-section {
  margin-top: 12px;
}

.review-item {
  padding: 16px 0 !important;
}

.login-link {
  color: #63e2b7;
  text-decoration: none;
  font-weight: bold;
  margin: 0 4px;
}

.login-link:hover {
  text-decoration: underline;
}

.person-link {
  color: inherit;
  text-decoration: none;
}

.person-link:hover {
  color: #63e2b7;
  text-decoration: underline;
}

.rating-distribution {
  margin-top: 16px;
  font-size: 12px;
}

.star-row {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
}

.star-row .n-text {
  width: 40px;
  flex-shrink: 0;
}

.percentage-text {
  width: 45px;
  text-align: left;
  padding-left: 8px;
  flex-shrink: 0;
}

.cast-list {
  padding-bottom: 16px;
  white-space: nowrap;
}

.person-card {
  display: inline-block;
  text-align: center;
  width: 80px;
  vertical-align: top;
}

.person-card a {
  text-decoration: none;
  color: inherit;
  display: block;
}

.person-name {
  margin-top: 8px;
  margin-bottom: 2px;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.person-role {
  margin: 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.52);
}
</style>