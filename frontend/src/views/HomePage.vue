<template>
  <n-layout-content content-style="padding: 24px;">
    <n-carousel autoplay show-arrow style="margin-bottom: 30px;">
      <img v-for="slide in carouselSlides" :key="slide.id" class="carousel-img" :src="slide.image">
    </n-carousel>

    <n-h2 prefix="bar">
      <n-text type="primary">热门电影</n-text>
    </n-h2>
    <n-spin :show="loading">
      <n-grid :x-gap="16" :y-gap="24" :cols="'2 s:3 m:4 l:5 xl:6'" responsive="true">
        <n-grid-item v-for="movie in popularMovies" :key="movie.id">
          <router-link :to="{ name: 'MovieDetail', params: { id: movie.id } }">
            <n-card :title="movie.title" hoverable content-style="padding:0;">
              <template #cover>
                <img :src="movie.posterUrl" class="movie-poster-img" :alt="movie.title">
              </template>
              <template #footer>
                <n-rate readonly :value="movie.averageRating / 2" allow-half size="small" />
              </template>
            </n-card>
          </router-link>
        </n-grid-item>
      </n-grid>
    </n-spin>

    <n-h2 prefix="bar" style="margin-top: 40px;">
      <n-text type="primary">最新电影</n-text>
    </n-h2>
    <n-spin :show="loading">
      <n-grid :x-gap="16" :y-gap="24" :cols="'2 s:3 m:4 l:5 xl:6'" responsive="true">
        <n-grid-item v-for="movie in latestMovies" :key="movie.id">
          <router-link :to="{ name: 'MovieDetail', params: { id: movie.id } }">
            <n-card :title="movie.title" hoverable content-style="padding:0;">
              <template #cover>
                <img :src="movie.posterUrl" class="movie-poster-img" :alt="movie.title">
              </template>
              <template #footer>
                <n-p depth="3" style="font-size: 12px; text-align: right;">
                  {{ movie.releaseYear }}
                </n-p>
              </template>
            </n-card>
          </router-link>
        </n-grid-item>
      </n-grid>
    </n-spin>
  </n-layout-content>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import {
  NLayoutContent, NText, NCarousel,
  NH2, NGrid, NGridItem, NCard, NRate, NSpin
} from 'naive-ui';
// 不再需要 import TheHeader
import apiService from '@/services/apiService';

const popularMovies = ref([]);
const latestMovies = ref([]); // 新增 ref
const loading = ref(true);

const carouselSlides = ref([
  { id: 1, image: 'https://picsum.photos/seed/carousel_scene_1/1200/500' },
  { id: 2, image: 'https://picsum.photos/seed/carousel_scene_2/1200/500' },
  { id: 3, image: 'https://picsum.photos/seed/carousel_scene_3/1200/500' },
]);

const fetchHotMovies = async () => {
  loading.value = true;
  try {
    const response = await apiService.getHotMovies(12);
    popularMovies.value = response.data;
  } catch (error) {
    console.error('获取热门电影失败:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(fetchHotMovies);
</script>

<style scoped>
.carousel-img {
  width: 100%;
  height: 500px;
  /* 可以调整轮播图高度 */
  object-fit: cover;
}

.movie-poster-img {
  width: 100%;
  aspect-ratio: 2 / 3;
  object-fit: cover;
}

:deep(.n-card-header__main) {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

a {
  text-decoration: none;
  color: inherit;
}
</style>