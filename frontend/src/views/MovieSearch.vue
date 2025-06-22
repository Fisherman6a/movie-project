<template>
  <n-layout-content content-style="padding: 24px;">
    <n-h2>
      搜索“<n-text type="primary">{{ keyword }}</n-text>”的结果
    </n-h2>

    <n-spin :show="searchStore.isLoading">
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
import apiService from '@/services/apiService';
import { NLayoutContent, NH2, NText, NSpin, NGrid, NGridItem, NCard, NEmpty } from 'naive-ui';

// 1. 引入我们新创建的 store
import { useSearchStore } from '@/stores/searchStore';

const props = defineProps({
  keyword: String,
});

const movies = ref([]);
// 2. 移除此组件内部的 loading 状态，因为它现在由 store 统一管理
// const loading = ref(false); 

// 3. 初始化 store
const searchStore = useSearchStore();

const fetchMovies = async (name) => {
  if (!name) return;

  // 4. 开始获取数据前，调用 store 的方法，将全局加载状态设为 true
  searchStore.startLoading();
  try {
    const actorRes = await apiService.searchMovies({ type: 'by-actor', name });
    const directorRes = await apiService.searchMovies({ type: 'by-director', name });

    const allMovies = [...actorRes.data, ...directorRes.data];
    movies.value = Array.from(new Map(allMovies.map(m => [m.id, m])).values());

  } catch (error) {
    console.error('搜索电影时出错:', error);
  } finally {
    // 5. 不论成功或失败，获取数据结束后，都调用 store 的方法，将全局加载状态设为 false
    searchStore.stopLoading();
  }
};

onMounted(() => {
  fetchMovies(props.keyword);
});

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