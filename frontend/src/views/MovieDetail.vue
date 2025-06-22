<template>
    <n-layout-content content-style="padding: 24px;">
      <n-spin :show="loading">
        <div v-if="movie">
          <n-grid :x-gap="24" :cols="4">
            <n-gi :span="1">
              <img :src="movie.posterUrl" class="poster-img" />
            </n-gi>
            <n-gi :span="3">
              <n-h1>{{ movie.title }} <n-text depth="3">({{ movie.releaseYear }})</n-text></n-h1>
              <n-p><strong>导演:</strong> {{ movie.directorNames.join(', ') }}</n-p>
              <n-p><strong>主演:</strong> {{ movie.actorNames.join(', ') }}</n-p>
              <n-p><strong>类型:</strong> {{ movie.genre }}</n-p>
              <n-p><strong>国家/地区:</strong> {{ movie.country }}</n-p>
              <n-p><strong>时长:</strong> {{ movie.duration }} 分钟</n-p>
              <n-p><strong>简介:</strong> {{ movie.synopsis }}</n-p>
              <n-flex align="center">
                <n-text strong>平均评分:</n-text>
                <n-rate readonly :value="movie.averageRating / 2" allow-half />
                <n-text strong>{{ movie.averageRating }}</n-text>
              </n-flex>
            </n-gi>
          </n-grid>
  
          <n-divider />
  
          <n-h2>评论区</n-h2>
          <div v-if="authStore.isAuthenticated">
            <n-h3>发表我的评分和评论</n-h3>
            <n-rate v-model:value="myRating" />
            <n-input
              v-model:value="myComment"
              type="textarea"
              placeholder="写下你的评论..."
              style="margin-top: 12px;"
            />
            <n-button type="primary" @click="submitReview" style="margin-top: 12px;">提交</n-button>
          </div>
          <n-alert v-else title="提示" type="info">
            请<router-link to="/login">登录</router-link>后发表评论。
          </n-alert>
  
          <n-list bordered style="margin-top: 24px;">
            <n-list-item v-for="review in reviews" :key="review.id">
              <n-thing :title="review.username" :description="review.commentText">
                <template #header-extra>
                  <n-text depth="3">{{ new Date(review.createdAt).toLocaleString() }}</n-text>
                </template>
              </n-thing>
            </n-list-item>
             <n-list-item v-if="reviews.length === 0">
                <n-empty description="暂无评论" />
             </n-list-item>
          </n-list>
        </div>
        <n-empty v-else description="电影信息加载失败" />
      </n-spin>
    </n-layout-content>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  // import { useRoute } from 'vue-router'; // <--- 移除这一行
  import apiService from '@/services/apiService';
  import { useAuthStore } from '@/stores/authStore';
  import { 
      NLayoutContent, NSpin, NGrid, NGi, NH1, NH2, NH3, NP, NText, NRate, NDivider, 
      NInput, NButton, NList, NListItem, NThing, NEmpty, NFlex, NAlert, useMessage
  } from 'naive-ui';
  // import TheHeader from '@/components/TheHeader.vue';
  
  // defineProps 是编译器宏，直接使用即可
  const props = defineProps({
    id: String
  });
  
  // const route = useRoute(); // <--- 移除这一行
  const message = useMessage();
  const authStore = useAuthStore();
  
  const movie = ref(null);
  const reviews = ref([]);
  const loading = ref(true);
  const myRating = ref(0);
  const myComment = ref('');
  
  const fetchMovieData = async () => {
    loading.value = true;
    try {
      const movieResponse = await apiService.getMovieById(props.id);
      movie.value = movieResponse.data;
      const reviewsResponse = await apiService.getReviewsForMovie(props.id);
      reviews.value = reviewsResponse.data;
    } catch (error) {
      message.error('加载电影数据失败');
      console.error(error);
    } finally {
      loading.value = false;
    }
  };
  
  const submitReview = async () => {
      if (!myComment.value) {
          message.warning('评论内容不能为空');
          return;
      }
      if (myRating.value > 0) {
          // 先提交评分
          await apiService.rateMovie(props.id, authStore.userId, myRating.value * 2);
      }
      // 再提交评论
      await apiService.addReview(props.id, authStore.userId, myComment.value);
  
      message.success('评论成功！');
      myComment.value = '';
      myRating.value = 0;
      fetchMovieData(); // 重新加载数据
  };
  
  onMounted(fetchMovieData);
  </script>
  
  <style scoped>
  .poster-img {
    width: 100%;
    border-radius: 8px;
  }
  </style>