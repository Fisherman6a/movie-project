<template>
  <div>
    <n-flex justify="space-between" align="center">
      <n-h2 style="margin: 0;">
        搜索“<n-text type="primary">{{ keyword }}</n-text>”的结果
      </n-h2>
      <router-link :to="{ name: 'PersonSearch', query: { keyword: keyword } }" class="more-link">
        > 搜索更多叫“{{ keyword }}”的影人
      </router-link>
    </n-flex>

    <n-spin :show="isLoading" style="margin-top: 20px;">
      <div v-if="moviesByTitle.length > 0" class="results-category">
        <n-h3 prefix="bar" type="info">相关电影</n-h3>
        <n-grid :x-gap="16" :y-gap="24" :cols="'2 s:3 m:4 l:5 xl:6'" responsive="true">
          <n-grid-item v-for="movie in moviesByTitle" :key="movie.id">
            <router-link :to="{ name: 'MovieDetail', params: { id: movie.id } }">
              <n-card :title="movie.title" hoverable content-style="padding:0;">
                <template #cover>
                  <img :src="movie.posterUrl" class="movie-poster-img">
                </template>
              </n-card>
            </router-link>
          </n-grid-item>
        </n-grid>
      </div>
      <div v-if="moviesByPerson.length > 0" class="results-category">
        <n-h3 prefix="bar" type="success">影人 “{{ keyword }}” 的相关作品</n-h3>
        <n-grid :x-gap="16" :y-gap="24" :cols="'2 s:3 m:4 l:5 xl:6'" responsive="true">
          <n-grid-item v-for="movie in moviesByPerson" :key="movie.id">
            <router-link :to="{ name: 'MovieDetail', params: { id: movie.id } }">
              <n-card :title="movie.title" hoverable content-style="padding:0;">
                <template #cover>
                  <img :src="movie.posterUrl" class="movie-poster-img">
                </template>
              </n-card>
            </router-link>
          </n-grid-item>
        </n-grid>
      </div>
      <n-empty v-if="!isLoading && moviesByTitle.length === 0 && moviesByPerson.length === 0"
        description="没有找到相关的电影或影人。">
      </n-empty>
    </n-spin>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { useMessage, NSpin, NH2, NH3, NText, NGrid, NGridItem, NCard, NEmpty, NFlex } from 'naive-ui';
import apiService from '@/services/apiService';

const props = defineProps({
  keyword: String,
});

const message = useMessage();
const isLoading = ref(false);

const moviesByTitle = ref([]);
const moviesByPerson = ref([]);

const performSearch = async (name) => {
  if (!name) return;

  isLoading.value = true;
  moviesByTitle.value = [];
  moviesByPerson.value = [];

  try {
    const [titleRes, actorRes, directorRes] = await Promise.all([
      apiService.searchMoviesByTitle(name),
      apiService.searchMovies({ type: 'by-actor', name }),
      apiService.searchMovies({ type: 'by-director', name }),
    ]);

    moviesByTitle.value = titleRes.data.content || [];

    const personMovies = [...actorRes.data, ...directorRes.data];
    moviesByPerson.value = Array.from(new Map(personMovies.map(m => [m.id, m])).values());

  } catch (error) {
    message.error('搜索时出错，请稍后重试。');
    console.error('搜索电影时出错:', error);
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  performSearch(props.keyword);
});

watch(() => props.keyword, (newKeyword) => {
  performSearch(newKeyword);
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

.results-category {
  margin-bottom: 40px;
}

/* 右上角链接样式 */
.more-link {
  font-size: 14px;
  color: #63e2b7;
  cursor: pointer;
}

.more-link:hover {
  text-decoration: underline;
}
</style>