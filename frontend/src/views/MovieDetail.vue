<template>
  <n-layout-content content-style="padding: 24px;">
    <n-spin :show="loading">
      <div v-if="movie">
        <!-- 1. 电影标题 -->
        <n-h1>
          <n-text style="font-weight: bold">{{ movie.title }}</n-text>
          <n-text :depth="3" style="font-size: 24px; margin-left: 12px">({{ movie.releaseYear }})</n-text>
        </n-h1>

        <!-- 2. 主体内容区 (海报 + 详细信息 + 评分) -->
        <n-grid :x-gap="24" :cols="10">
          <!-- 2.1 左侧海报 -->
          <n-gi :span="3">
            <img :src="movie.posterUrl" class="poster-img" :alt="movie.title" />
          </n-gi>

          <!-- 2.2 中间详细信息 -->
          <n-gi :span="5">
            <n-space vertical size="small" class="movie-meta">
              <p>
                <n-text class="movie-meta-key">导演:</n-text>
                {{ movie.directorNames.join(" / ") }}
              </p>
              <p>
                <n-text class="movie-meta-key">主演:</n-text>
                {{ movie.actorNames.join(" / ") }}
              </p>
              <p>
                <n-text class="movie-meta-key">类型:</n-text> {{ movie.genre }}
              </p>
              <p>
                <n-text class="movie-meta-key">国家/地区:</n-text>
                {{ movie.country }}
              </p>
              <p>
                <n-text class="movie-meta-key">语言:</n-text>
                {{ movie.language }}
              </p>
              <p>
                <n-text class="movie-meta-key">片长:</n-text>
                {{ movie.duration }} 分钟
              </p>
            </n-space>
          </n-gi>

          <!-- 2.3 右侧评分卡 -->
          <n-gi :span="2">
            <n-card size="small" class="rating-box">
              <n-text depth="3">项目评分</n-text>
              <n-flex align="baseline" justify="center">
                <n-text style="font-size: 32px; font-weight: bold">{{
                  movie.averageRating.toFixed(1)
                }}</n-text>
              </n-flex>
              <n-rate readonly :value="movie.averageRating / 2" allow-half />
              <n-p depth="3" style="font-size: 12px; margin-top: 8px">
                {{ reviews.length }} 人评价
              </n-p>
            </n-card>
          </n-gi>
        </n-grid>

        <!-- 3. 用户操作区 -->
        <div class="action-section">
          <n-h3>评价</n-h3>
          <div v-if="authStore.isAuthenticated">
            <n-rate v-model:value="myRating" size="large" />
            <n-p v-if="myRating > 0" depth="3" style="margin-left: 12px">
              {{ ratingText[myRating - 1] }}
            </n-p>
          </div>
          <n-alert v-else title="提示" type="info" :bordered="false" style="width: fit-content">
            请<router-link to="/login" class="login-link">登录</router-link>后进行评价。
          </n-alert>
        </div>

        <!-- 4. 剧情简介 -->
        <n-h2>剧情简介</n-h2>
        <n-p class="synopsis-text">{{ movie.synopsis }}</n-p>

        <!-- 5. 演职员表 (简化版) -->
        <n-h2>演职员表</n-h2>
        <p>此部分为简化展示，未来可扩展为带图片的滚动列表。</p>

        <!-- 6. 影评区 -->
        <n-h2>影评</n-h2>
        <div v-if="authStore.isAuthenticated" class="review-input-section">
          <n-input v-model:value="myComment" type="textarea" placeholder="你看过这部电影吗？快来写下你的影评吧..."
            :autosize="{ minRows: 3, maxRows: 6 }" />
          <n-button type="primary" @click="submitReview" :loading="isSubmitting" style="margin-top: 12px">
            提交
          </n-button>
        </div>

        <n-list bordered separator style="margin-top: 24px; background-color: #1a1a1f">
          <template #header>
            <n-text strong>热门影评</n-text>
          </template>
          <n-list-item v-for="review in reviews" :key="review.id" class="review-item">
            <n-thing>
              <template #avatar>
                <!-- 头像占位，未来可扩展 -->
                <n-avatar round size="medium">{{
                  review.username.charAt(0)
                }}</n-avatar>
              </template>
              <template #header>
                <n-text strong>{{ review.username }}</n-text>
              </template>
              <template #header-extra>
                <n-text depth="3">{{
                  new Date(review.createdAt).toLocaleDateString()
                }}</n-text>
              </template>
              <p>{{ review.commentText }}</p>
              <!-- ========== START: 点赞/点踩区域 ========== -->
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
import { ref, onMounted } from "vue";
import apiService from "@/services/apiService";
import { useAuthStore } from "@/stores/authStore";
import { ThumbsUpOutline as ThumbsUpIcon, ThumbsDownOutline as ThumbsDownIcon } from '@vicons/ionicons5';
import {
  NLayoutContent, NSpin, NGrid, NGi, NH1, NH2, NH3, NP, NText, NRate, NInput, NButton, NList, NListItem,
  NThing, NEmpty, NFlex, NAlert, useMessage, NCard, NSpace, NAvatar, NIcon
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

// 用户评分和评论
const myRating = ref(0);
const myComment = ref("");
const ratingText = ["很差", "较差", "还行", "推荐", "力荐"];

const fetchMovieData = async () => {
  loading.value = true;
  try {
    const movieResponse = await apiService.getMovieById(props.id);
    movie.value = movieResponse.data;
    const reviewsResponse = await apiService.getReviewsForMovie(props.id);
    reviews.value = reviewsResponse.data.sort(
      (a, b) => new Date(b.createdAt) - new Date(a.createdAt)
    );
  } catch (error) {
    message.error("加载电影数据失败");
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const submitReview = async () => {
  if (!myRating.value && !myComment.value) {
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
    await fetchMovieData(); // 重新加载数据以显示最新评价
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
    // 更新前端数据，避免重新加载整个列表
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

.movie-meta-key {
  color: rgba(255, 255, 255, 0.52);
  /* 柔和的标签颜色 */
  margin-right: 8px;
}

.movie-meta p {
  margin: 4px 0;
}

.rating-box {
  text-align: center;
  background-color: #1a1a1f;
  /* 稍暗的背景以突出 */
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.action-section {
  margin: 24px 0;
}

.synopsis-text {
  line-height: 1.7;
  text-indent: 2em;
  /* 首行缩进 */
}

.review-input-section {
  margin-bottom: 24px;
}

.review-item {
  padding: 16px 0 !important;
}

.login-link {
  color: #63e2b7;
  /* Naive UI 主色调 */
  text-decoration: none;
  font-weight: bold;
  margin: 0 4px;
}

.login-link:hover {
  text-decoration: underline;
}
</style>
