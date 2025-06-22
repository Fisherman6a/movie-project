<template>
    <n-layout style="height: 100vh">
      <n-layout-header bordered style="padding: 12px 24px;">
        <n-flex justify="space-between" align="center">
          <n-text tag="div" style="font-size: 24px; font-weight: bold;">
            🎬 影评时光
          </n-text>
          <n-input-group style="max-width: 400px;">
            <n-input :style="{ width: '80%' }" placeholder="搜索电影、演员..." v-model:value="searchTerm" />
            <n-button type="primary" ghost @click="handleSearch">
              搜索
            </n-button>
          </n-input-group>
          <n-space>
            <n-button text>首页</n-button>
            <n-button text>电影</n-button>
            <n-button text>电视剧</n-button>
            <n-button text strong secondary type="primary">登录</n-button>
            <n-button text strong secondary type="success">注册</n-button>
          </n-space>
        </n-flex>
      </n-layout-header>
  
      <n-layout-content content-style="padding: 24px;">
        <n-carousel autoplay show-arrow style="margin-bottom: 30px; border-radius: 8px; overflow: hidden;">
          <img
            v-for="(slide, index) in carouselSlides"
            :key="index"
            class="carousel-img"
            :src="slide.image"
            :alt="slide.alt"
          >
        </n-carousel>
  
        <n-h2 prefix="bar">
          <n-text type="primary">热门电影</n-text>
        </n-h2>
        <n-grid :x-gap="16" :y-gap="24" :cols="'2 s:3 m:4 l:5 xl:6'" :responsive="true">
          <n-grid-item v-for="movie in popularMovies" :key="movie.id">
            <n-card :title="movie.title" hoverable content-style="padding:0;" footer-style="padding:12px">
              <template #cover>
                <img :src="movie.poster" class="movie-poster-img" :alt="movie.title">
              </template>
              <template #footer>
                <n-flex justify="space-between" align="center">
                  <n-rate readonly :default-value="movie.rating" size="small" />
                  <n-text depth="3" style="font-size: 12px;">{{ movie.year }}</n-text>
                </n-flex>
              </template>
            </n-card>
          </n-grid-item>
        </n-grid>
  
        <n-divider />
  
        <n-h2 prefix="bar">
           <n-text type="success">最新上映</n-text>
        </n-h2>
         <n-grid :x-gap="16" :y-gap="24" :cols="'2 s:3 m:4 l:5 xl:6'" :responsive="true">
          <n-grid-item v-for="movie in latestMovies" :key="movie.id">
            <n-card :title="movie.title" hoverable content-style="padding:0;" footer-style="padding:12px">
              <template #cover>
                <img :src="movie.poster" class="movie-poster-img" :alt="movie.title">
              </template>
              <template #footer>
                <n-flex justify="space-between" align="center">
                  <n-tag :type="movie.rating > 4 ? 'success' : (movie.rating > 3 ? 'warning' : 'error')" size="small">
                    评分: {{ movie.rating.toFixed(1) }}
                  </n-tag>
                  <n-button size="tiny" type="primary" ghost>查看详情</n-button>
                </n-flex>
              </template>
            </n-card>
          </n-grid-item>
        </n-grid>
  
      </n-layout-content>
  
      <n-layout-footer bordered position="static" style="padding: 24px; text-align: center;">
        <n-text depth="3">
          © {{ new Date().getFullYear() }} 影评时光. All Rights Reserved.
        </n-text>
      </n-layout-footer>
    </n-layout>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import {
    NLayout, NLayoutHeader, NLayoutContent, NLayoutFooter,
    NText, NInput, NInputGroup, NButton, NSpace, NFlex,
    NCarousel,
    NH2, NGrid, NGridItem, NCard, NRate, NTag, NDivider
  } from 'naive-ui';
  
  const searchTerm = ref('');
  
  const handleSearch = () => {
    if (searchTerm.value.trim()) {
      alert(`正在搜索: ${searchTerm.value}`);
      // 在这里实现实际的搜索逻辑，例如跳转到搜索结果页
    }
  };
  
  const carouselSlides = ref([
  { image: 'https://picsum.photos/seed/carousel_scene_1/800/350', alt: '电影场景 1' },
  { image: 'https://picsum.photos/seed/carousel_scene_2/800/350', alt: '电影场景 2' },
  { image: 'https://picsum.photos/seed/carousel_scene_3/800/350', alt: '电影场景 3' },
]);

// 示例电影数据 (实际应用中你会从API获取)
const popularMovies = ref([
  { id: 1, title: '电影A：史诗冒险', rating: 4.5, year: '2023', poster: 'https://picsum.photos/seed/movie_a/300/450' },
  { id: 2, title: '电影B：城市迷踪', rating: 4.0, year: '2022', poster: 'https://picsum.photos/seed/movie_b/300/450' },
  { id: 3, title: '电影C：未来幻想', rating: 5.0, year: '2023', poster: 'https://picsum.photos/seed/movie_c/300/450' },
  { id: 4, title: '电影D：温馨家庭', rating: 3.5, year: '2021', poster: 'https://picsum.photos/seed/movie_d/300/450' },
  { id: 5, title: '电影E：午夜惊魂', rating: 4.2, year: '2023', poster: 'https://picsum.photos/seed/movie_e/300/450' },
  { id: 6, title: '电影F：青春恋曲', rating: 3.8, year: '2022', poster: 'https://picsum.photos/seed/movie_f/300/450' },
]);

const latestMovies = ref([
  { id: 7, title: '新片X：代码危机', rating: 4.8, poster: 'https://picsum.photos/seed/new_x/300/450' },
  { id: 8, title: '新片Y：遗失的宝藏', rating: 3.2, poster: 'https://picsum.photos/seed/new_y/300/450' },
  { id: 9, title: '新片Z：星际穿越者', rating: 4.1, poster: 'https://picsum.photos/seed/new_z/300/450' },
  { id: 10, title: '新片W：古堡疑云', rating: 2.9, poster: 'https://picsum.photos/seed/new_w/300/450' },
]);

</script>
  
  <style scoped>
  .carousel-img {
    width: 100%;
    height: 350px; /* 你可以调整轮播图的高度 */
    object-fit: cover;
  }
  
  .movie-poster-img {
    width: 100%;
    height: auto; /* 高度自适应，保持图片比例 */
    aspect-ratio: 2 / 3; /* 常见海报比例 */
    object-fit: cover;
  }
  
  /* 确保卡片标题不会太长而换行，或者你可以设置固定高度并允许溢出省略 */
  :deep(.n-card-header__main) {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  </style>