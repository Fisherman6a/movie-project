<template>
    <!-- <TheHeader /> -->
    <n-layout-content content-style="padding: 24px;">
      <n-h2>
        搜索“<n-text type="primary">{{ keyword }}</n-text>”的结果
      </n-h2>
  
      <n-spin :show="loading">
        <n-grid v-if="movies.length > 0" :x-gap="16" :y-gap="24" :cols="'2 s:3 m:4 l:5 xl:6'" responsive="true">
          <n-grid-item v-for="movie in movies" :key="movie.id">
              <router-link :to="{ name: 'MovieDetail', params: { id: movie.id } }">
                  <n-card :title="movie.title" hoverable content-style="padding:0;">
                      <template #cover>
                          <img :src="movie.posterUrl" class="movie-poster-img">
                      </template>
                  </n-card>
              </router-link>
          </n-grid-item>
        </n-grid>
        <n-empty v-else description="没有找到相关的电影。">
        </n-empty>
      </n-spin>
  
    </n-layout-content>
  </template>
  
<script setup>
import { ref, onMounted, watch } from 'vue';
// import { useRoute } from 'vue-router'; // <--- 移除这一行
import apiService from '@/services/apiService';
import { NLayoutContent, NH2, NText, NSpin, NGrid, NGridItem, NCard, NEmpty } from 'naive-ui';
// import TheHeader from '@/components/TheHeader.vue';

const props = defineProps({
keyword: String,
});

// const route = useRoute(); // <--- 移除这一行
const movies = ref([]);
const loading = ref(false);

const fetchMovies = async (name) => {
if (!name) return;
loading.value = true;
try {
  const actorRes = await apiService.searchMovies({ type: 'by-actor', name });
  const directorRes = await apiService.searchMovies({ type: 'by-director', name });
  
  const allMovies = [...actorRes.data, ...directorRes.data];
  movies.value = Array.from(new Map(allMovies.map(m => [m.id, m])).values());

} catch (error) {
  console.error('搜索电影时出错:', error);
} finally {
  loading.value = false;
}
};

onMounted(() => {
fetchMovies(props.keyword);
});

// 这里监听 props 的变化是正确的
watch(() => props.keyword, (newKeyword) => {
fetchMovies(newKeyword);
});
</script>

<style scoped>
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