<template>
    <div>
      <n-h1 class="text-3xl font-bold mb-6">发现电影</n-h1>
      <n-card class="mb-6" style="background-color: #2a2a2a;">
          <n-space :size="24">
              <n-input v-model:value="searchParams.releaseYear" placeholder="按年份搜索" clearable @keydown.enter="handleSearch"/>
              <n-input v-model:value="searchParams.genre" placeholder="按类型搜索" clearable @keydown.enter="handleSearch"/>
              <n-input v-model:value="searchParams.country" placeholder="按国家搜索" clearable @keydown.enter="handleSearch"/>
              <n-button type="primary" @click="handleSearch">搜索</n-button>
          </n-space>
      </n-card>
  
      <n-spin :show="loading">
          <div v-if="movies.length > 0" class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-6">
              <MovieCard v-for="movie in movies" :key="movie.id" :movie="movie" />
          </div>
          <n-empty v-else description="没有找到符合条件的电影" size="huge" class="py-16" />
      </n-spin>
  
      <div class="flex justify-center mt-8">
          <n-pagination
              v-model:page="page"
              :page-count="totalPages"
              @update:page="onPageChange"
          />
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, reactive } from 'vue';
  import { searchMovies } from '@/api/movies';
  import MovieCard from '@/components/movie/MovieCard.vue';
  import { NCard, NSpace, NInput, NButton, NSpin, NEmpty, NPagination } from 'naive-ui';
  import { useMessage } from 'naive-ui';
  
  const message = useMessage();
  const loading = ref(true);
  const movies = ref([]);
  const page = ref(1);
  const totalPages = ref(1);
  
  const searchParams = reactive({
      releaseYear: null,
      genre: '',
      country: '',
      minRating: null,
      sortBy: 'releaseYear',
      sortDir: 'desc',
      size: 12,
  });
  
  async function fetchMovies() {
      loading.value = true;
      try {
          const params = { ...searchParams, page: page.value - 1 };
          const response = await searchMovies(params);
          movies.value = response.data.content;
          totalPages.value = response.data.totalPages;
      } catch (error) {
          message.error('加载电影列表失败');
      } finally {
          loading.value = false;
      }
  }
  
  function handleSearch() {
      page.value = 1;
      fetchMovies();
  }
  
  function onPageChange(currentPage) {
      page.value = currentPage;
      fetchMovies();
  }
  
  onMounted(fetchMovies);
  </script>
  
  <style scoped>
  .grid {
    display: grid;
  }
  </style>