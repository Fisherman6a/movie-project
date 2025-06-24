<template>
  <div v-if="movie">
    <n-h1>
      <n-text style="font-weight: bold;">{{ movie.title }}</n-text>
      <n-text :depth="3" style="font-size: 24px; margin-left: 12px;">({{ movie.releaseYear }})</n-text>
    </n-h1>

    <n-grid :x-gap="24" :cols="10">
      <n-gi :span="3">
        <img :src="movie.posterUrl" class="poster-img" :alt="movie.title" />
      </n-gi>
      <n-gi :span="4">
        <n-space vertical size="small" class="movie-meta">
          <p><n-text class="meta-key">导演:</n-text>
            <router-link v-for="(director, index) in movie.directors" :key="director.id"
              :to="{ name: 'DirectorDetail', params: { id: director.id } }" class="person-link">
              {{ director.name }}<span v-if="index < movie.directors.length - 1"> / </span>
            </router-link>
          </p>
          <p><n-text class="meta-key">主演:</n-text>
            <router-link v-for="(actor, index) in movie.cast" :key="actor.id"
              :to="{ name: 'ActorDetail', params: { id: actor.id } }" class="person-link">
              {{ actor.name }}<span v-if="index < movie.cast.length - 1"> / </span>
            </router-link>
          </p>
          <p><n-text class="meta-key">类型:</n-text> {{ movie.genre }}</p>
          <p><n-text class="meta-key">国家/地区:</n-text> {{ movie.country }}</p>
          <p><n-text class="meta-key">语言:</n-text> {{ movie.language }}</p>
          <p><n-text class="meta-key">片长:</n-text> {{ movie.duration }} 分钟</p>
        </n-space>
      </n-gi>
      <n-gi :span="3">
        <n-card size="small" class="rating-box">
          <n-text depth="3">项目评分</n-text>
          <n-flex align="baseline" justify="center">
            <n-text style="font-size: 32px; font-weight: bold;">
              {{ typeof movie.averageRating === 'number' ? movie.averageRating.toFixed(1) : '暂无' }}
            </n-text>
          </n-flex>
          <n-flex justify="center" align="center">
            <n-rate readonly :value="(movie.averageRating || 0) / 2" allow-half />
            <n-text depth="3" style="margin-left: 8px;">{{ averageRatingDescription }}</n-text>
          </n-flex>
          <div class="rating-distribution">
            <div v-for="star in 5" :key="star" class="star-row">
              <n-text class="star-label">{{ 6 - star }} 星</n-text>
              <n-progress type="line" :percentage="getStarPercentage(6 - star)" :show-indicator="false" color="#f2c038"
                rail-color="rgba(255, 255, 255, 0.12)" style="flex-grow: 1;" />
              <n-text depth="3" class="percentage-text">{{ getStarPercentage(6 - star).toFixed(1) }}%</n-text>
            </div>
          </div>
        </n-card>
      </n-gi>
    </n-grid>

    <n-grid :x-gap="24" :cols="10" style="margin-top: 24px;">
      <n-gi :span="10">
        <n-h2>剧情简介</n-h2>
        <n-p class="synopsis-text">{{ movie.synopsis }}</n-p>
        <n-h2>演职员表</n-h2>
        <n-scrollbar x-scrollable>
          <n-space :size="24" class="cast-list">
            <div v-for="person in movie.directors" :key="`dir-${person.id}`" class="person-card">
              <router-link :to="{ name: 'DirectorDetail', params: { id: person.id } }">
                <img :src="person.profileImageUrl" class="person-image" :alt="person.name" />
                <p class="person-name">{{ person.name }}</p>
                <p class="person-role">导演</p>
              </router-link>
            </div>
            <div v-for="person in movie.cast" :key="`act-${person.id}`" class="person-card">
              <router-link :to="{ name: 'ActorDetail', params: { id: person.id } }">
                <img :src="person.profileImageUrl" class="person-image" :alt="person.name" />
                <p class="person-name">{{ person.name }}</p>
                <p class="person-role">演员</p>
              </router-link>
            </div>
          </n-space>
        </n-scrollbar>
      </n-gi>
    </n-grid>

    <div style="margin-top: 24px;">
      <n-h2>评价</n-h2>
      <div v-if="authStore.isAuthenticated" class="review-input-section">
        <n-space align="center">
          <n-rate v-model:value="myRating" size="large" />
          <n-text v-if="myRating > 0" depth="2" style="font-size: 16px;">
            {{ ratingText[myRating - 1] }}
          </n-text>
        </n-space>
        <n-input v-model:value="myComment" type="textarea" placeholder="写下你的短评..."
          :autosize="{ minRows: 4, maxRows: 8 }" style="margin-top: 12px;" />
        <n-button type="primary" @click="submitReview" :loading="isSubmitting" style="margin-top: 12px; width: 100%;">
          提交
        </n-button>
      </div>
      <n-alert v-else title="提示" type="info" :bordered="false">
        请<router-link to="/login" class="login-link">登录</router-link>后发表评论。
      </n-alert>
    </div>

    <n-h2 style="margin-top: 24px;">热门影评</n-h2>
    <n-list bordered separator style="background-color: #1a1a1f;">
      <n-list-item v-for="review in reviews" :key="review.id" class="review-item">
        <n-thing>
          <template #avatar>
            <n-avatar round :src="review.userProfileImageUrl" />
          </template>
          <template #header>
            <div class="review-header-line">
              <n-space align="center" :size="8" :wrap-item="false">
                <n-text class="review-username">{{ review.username }}</n-text>
                <n-rate v-if="review.score" readonly :value="review.score / 2" size="small" />
                <n-text depth="3">看过 @</n-text>
                <n-text depth="3">{{ formatDateTime(review.createdAt) }}</n-text>
              </n-space>
            </div>
          </template>
          <template #header-extra>
            <n-space v-if="authStore.isAuthenticated && review.userId === authStore.userId">
              <n-button text @click="openEditModal(review)">
                <template #icon><n-icon :component="CreateIcon" size="20" /></template>
              </n-button>
              <n-popconfirm @positive-click="handleDeleteReview(review.id)" :show-icon="false">
                <template #trigger>
                  <n-button text type="error">
                    <template #icon><n-icon :component="TrashIcon" size="20" /></template>
                  </n-button>
                </template>
                确定要删除这条评论吗？
              </n-popconfirm>
            </n-space>
          </template>
          <p class="review-comment-text">{{ review.commentText }}</p>
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

  <n-modal v-model:show="showEditModal" preset="card" style="width: 600px" title="编辑评论">
    <n-input v-model:value="editingReview.commentText" type="textarea" placeholder="输入新的评论内容..."
      :autosize="{ minRows: 5 }" />
    <template #footer>
      <n-flex justify="end">
        <n-button @click="showEditModal = false">取消</n-button>
        <n-button type="primary" @click="handleUpdateReview" :loading="isUpdating">保存修改</n-button>
      </n-flex>
    </template>
  </n-modal>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import apiService from '@/services/apiService';
import { useAuthStore } from '@/stores/authStore';
import {
  ThumbsUpOutline as ThumbsUpIcon,
  ThumbsDownOutline as ThumbsDownIcon,
  CreateOutline as CreateIcon,
  TrashOutline as TrashIcon,
} from '@vicons/ionicons5';
import {
  NGrid, NGi, NH1, NH2, NP, NText, NRate, NInput, NButton, NList, NListItem,
  NThing, NEmpty, NFlex, NAlert, useMessage, NCard, NSpace, NAvatar, NIcon, NProgress, NScrollbar,
  NModal, NPopconfirm
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

const showEditModal = ref(false);
const isUpdating = ref(false);
const editingReview = ref({ id: null, commentText: '' });

const averageRatingDescription = computed(() => {
  if (typeof movie.value?.averageRating !== 'number' || movie.value.averageRating <= 0) {
    return '';
  }
  const scoreIn5 = movie.value.averageRating / 2;
  const clampedScore = Math.max(1, Math.min(5, scoreIn5));
  const index = Math.ceil(clampedScore) - 1;
  return ratingText[index];
});

const openEditModal = (review) => {
  editingReview.value = { ...review };
  showEditModal.value = true;
};

const handleUpdateReview = async () => {
  if (!editingReview.value.commentText.trim()) {
    message.warning('评论内容不能为空');
    return;
  }
  isUpdating.value = true;
  try {
    // 假设 updateReview API 可以接受包含评论和分数的新对象
    await apiService.updateReview(editingReview.value.id, {
      commentText: editingReview.value.commentText,
      score: editingReview.value.score // 确保分数也被传递
    });
    message.success('评论更新成功');
    showEditModal.value = false;
    await fetchMovieData();
  } catch (error) {
    message.error('更新失败');
    console.error(error);
  } finally {
    isUpdating.value = false;
  }
};

const handleDeleteReview = async (reviewId) => {
  try {
    await apiService.deleteReview(reviewId);
    message.success('删除成功');
    await fetchMovieData();
  } catch (error) {
    message.error('删除失败');
    console.error(error);
  }
};

const formatDateTime = (isoString) => {
  if (!isoString) return '';
  const date = new Date(isoString);
  const pad = (num) => num.toString().padStart(2, '0');
  const year = date.getFullYear();
  const month = pad(date.getMonth() + 1);
  const day = pad(date.getDate());
  const hour = pad(date.getHours());
  const minute = pad(date.getMinutes());
  return `${year}-${month}-${day} ${hour}:${minute}`;
};

const getStarPercentage = (star) => {
  if (!movie.value || !movie.value.ratingDistribution) return 0;
  const found = movie.value.ratingDistribution.find(d => d.star === star);
  return found ? found.percentage : 0;
};

const fetchMovieData = async () => {
  loading.value = true;
  try {
    const [movieResponse, reviewsResponse] = await Promise.all([
      apiService.getMovieById(props.id),
      apiService.getReviewsForMovie(props.id)
    ]);
    movie.value = movieResponse.data;
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
  if (myRating.value === 0 || !myComment.value.trim()) {
    message.warning("请同时提供评分和评论内容。");
    return;
  }
  isSubmitting.value = true;
  try {
    await apiService.addReview(
      props.id,
      authStore.userId,
      myRating.value, // 1-5 星
      myComment.value.trim()
    );
    message.success("评价成功！");
    myComment.value = "";
    myRating.value = 0;
    await fetchMovieData(); // 重新加载数据
  } catch (error) {
    message.error(error.response?.data?.message || "提交失败，可能您已评价过该电影。");
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

.movie-meta p {
  margin: 4px 0;
}

.meta-key {
  color: rgba(255, 255, 255, 0.52);
  margin-right: 8px;
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
  padding: 24px !important;
}

:deep(.n-thing-header) {
  display: flex;
  align-items: center;
}

.review-header-line {
  line-height: 1;
}

.review-comment-text {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.82);
  line-height: 1.6;
  margin-top: 12px;
  margin-bottom: 12px;
}

.review-username {
  font-weight: bold;
  color: #63e2b7;
}

.login-link,
.person-link {
  color: inherit;
  text-decoration: none;
}

.person-link:hover {
  text-decoration: underline;
  color: #63e2b7;
}

.rating-distribution {
  margin-top: 16px;
  font-size: 12px;
}

.star-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.star-label {
  flex-shrink: 0;
  white-space: nowrap;
}

.percentage-text {
  flex-shrink: 0;
  min-width: 45px;
  text-align: right;
}

.cast-list {
  padding-bottom: 16px;
  white-space: nowrap;
}

.person-card {
  display: inline-block;
  text-align: center;
  width: 120px;
  vertical-align: top;
}

.person-card a {
  text-decoration: none;
  color: inherit;
  display: block;
}

.person-image {
  width: 108px;
  height: 160px;
  object-fit: cover;
  border-radius: 3px;
  background-color: #333;
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