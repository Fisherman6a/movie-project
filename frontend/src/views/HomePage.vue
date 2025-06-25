<template>
  <n-layout-content>
    <n-carousel autoplay show-arrow style="margin-bottom: 30px;">
      <img v-for="slide in carouselSlides" :key="slide.id" class="carousel-img" :src="slide.image">
    </n-carousel>

    <n-spin :show="loading">
      <n-flex justify="space-between" align="center">
        <n-h2 prefix="bar" style="margin: 0;">
          <n-text type="primary">热门电影</n-text>
        </n-h2>
        <router-link :to="{ path: '/browser', query: { quickFilter: 'hot' } }" class="more-link">
          <n-button text>
            查看更多
            <template #icon>
              <n-icon :component="ArrowForwardIcon" />
            </template>
          </n-button>
        </router-link>
      </n-flex>
      <n-grid :x-gap="16" :y-gap="24" :cols="'2 s:3 m:4 l:5 xl:6'" responsive="true" style="margin-top: 16px;">
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

      <n-flex justify="space-between" align="center" style="margin-top: 40px;">
        <n-h2 prefix="bar" style="margin: 0;">
          <n-text type="primary">最新电影</n-text>
        </n-h2>
        <router-link :to="{ path: '/browser', query: { quickFilter: 'latest' } }" class="more-link">
          <n-button text>
            查看更多
            <template #icon>
              <n-icon :component="ArrowForwardIcon" />
            </template>
          </n-button>
        </router-link>
      </n-flex>
      <n-grid :x-gap="16" :y-gap="24" :cols="'2 s:3 m:4 l:5 xl:6'" responsive="true" style="margin-top: 16px;">
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

      <template #description>
        正在加载电影...
      </template>
      <div v-if="loading" style="min-height: 400px;"></div>
    </n-spin>
  </n-layout-content>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import {
  NLayoutContent, NText, NCarousel, NH2, NGrid, NGridItem, NCard,
  NRate, NSpin, NFlex, NButton, NIcon, NP
} from 'naive-ui';
import { ArrowForwardOutline as ArrowForwardIcon } from '@vicons/ionicons5';
import apiService from '@/services/apiService';

const popularMovies = ref([]);
const latestMovies = ref([]);
const loading = ref(true);

const carouselSlides = ref([
  {
    id: 1, image: 'https://i.ibb.co/FbFz1v2s/pexels-pixabay-274937.jpg'
  },
  { id: 2, image: 'https://i.ibb.co/NgdsQg4v/pexels-markusspiske-4201333.jpg' },
  {
    id: 3, image: 'https://i.ibb.co/W4g4P1Fp/pexels-tima-miroshnichenko-7991158.jpg'
  },
]);
const fetchHomePageMovies = async () => {
  loading.value = true;
  try {
    const [hotResponse, latestResponse] = await Promise.all([
      apiService.getHotMovies(12),
      // **核心修正**: 将数字 12 改为参数对象
      apiService.getLatestMovies({ sortBy: 'releaseYear', sortDir: 'desc', size: 12 })
    ]);
    popularMovies.value = hotResponse.data;
    latestMovies.value = latestResponse.data.content;
  } catch (error) {
    console.error('获取首页电影失败:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(fetchHomePageMovies);
</script>

<style scoped>
.carousel-img {
  width: 100%;
  height: 400px;
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

.more-link {
  color: rgba(255, 255, 255, 0.52);
  transition: color 0.3s;
}

.more-link:hover {
  color: #63e2b7;
}
</style>