<template>
  <n-layout-content content-style="padding: 24px;">
    <n-h2 prefix="bar">
      <n-text type="primary">选电影</n-text>
    </n-h2>

    <n-space align="center" style="margin-bottom: 20px">
      <n-button text :type="quickFilter === 'all' ? 'primary' : 'default'" @click="handleQuickFilter('all')"
        tag="a">全部</n-button>
      <n-divider vertical />
      <n-button text :type="quickFilter === 'hot' ? 'primary' : 'default'" @click="handleQuickFilter('hot')"
        tag="a">热门电影</n-button>
      <n-button text :type="quickFilter === 'latest' ? 'primary' : 'default'" @click="handleQuickFilter('latest')"
        tag="a">最新电影</n-button>
      <n-button text :type="quickFilter === 'top_rated' ? 'primary' : 'default'" @click="handleQuickFilter('top_rated')"
        tag="a">高分佳片</n-button>
    </n-space>

    <n-card size="small">
      <n-space vertical :size="20">
        <n-space align="center">
          <n-text style="width: 50px">类型:</n-text>
          <n-space>
            <n-tag v-for="tag in genreTags" :key="tag.value" checkable :checked="filterParams.genre === tag.value"
              @click="selectFilter('genre', tag.value)">
              {{ tag.label }}
            </n-tag>
          </n-space>
        </n-space>
        <n-space align="center">
          <n-text style="width: 50px">地区:</n-text>
          <n-space>
            <n-tag v-for="tag in countryTags" :key="tag.value" checkable :checked="filterParams.country === tag.value"
              @click="selectFilter('country', tag.value)">
              {{ tag.label }}
            </n-tag>
          </n-space>
        </n-space>
        <n-space align="center">
          <n-text style="width: 50px">年代:</n-text>
          <n-space>
            <n-tag v-for="tag in yearTags" :key="tag.value" checkable :checked="filterParams.releaseYear === tag.value"
              @click="selectFilter('releaseYear', tag.value)">
              {{ tag.label }}
            </n-tag>
          </n-space>
        </n-space>
      </n-space>

      <n-divider />

      <n-space align="center">
        <n-text>其他选项:</n-text>
        <n-checkbox disabled>未看过 (功能待定)</n-checkbox>
        <n-checkbox disabled>可播放 (功能待定)</n-checkbox>
      </n-space>
    </n-card>

    <n-spin :show="loading" style="margin-top: 24px">
      <n-grid v-if="movies.length > 0" :x-gap="16" :y-gap="24" :cols="'2 s:3 m:4 l:5 xl:6'" responsive="true">
        <n-grid-item v-for="movie in movies" :key="movie.id">
          <router-link :to="{ name: 'MovieDetail', params: { id: movie.id } }">
            <n-card :title="movie.title" hoverable content-style="padding:0;">
              <template #cover>
                <img :src="movie.posterUrl" class="movie-poster-img" />
              </template>
              <template #footer>
                <n-rate readonly :value="movie.averageRating / 2" allow-half size="small" />
              </template>
            </n-card>
          </router-link>
        </n-grid-item>
      </n-grid>
      <n-empty v-else description="没有找到符合条件的电影。" style="padding: 48px 0" />
    </n-spin>

    <n-flex justify="center" style="margin-top: 24px">
      <n-pagination v-model:page="pagination.page" :page-count="pagination.pageCount" @update:page="handlePageChange" />
    </n-flex>
  </n-layout-content>
</template>

<script setup>
import { ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import apiService from "@/services/apiService";
import {
  NLayoutContent,
  NGrid,
  NCard,
  NSpace,
  NTag,
  NCheckbox,
  NRate,
  NButton,
  NH2,
  NSpin,
  NEmpty,
  NPagination,
  NFlex,
  NDivider,
  NText,
} from "naive-ui";

const route = useRoute();
const router = useRouter();
const loading = ref(true);
const movies = ref([]);

const genreTags = [
  { label: "全部", value: null }, { label: "剧情", value: "剧情" }, { label: "喜剧", value: "喜剧" },
  { label: "科幻", value: "科幻" }, { label: "动作", value: "动作" }, { label: "爱情", value: "爱情" },
  { label: "动画", value: "动画" }, { label: "悬疑", value: "悬疑" }, { label: "惊悚", value: "惊悚" },
];

const countryTags = [
  { label: "全部", value: null }, { label: "中国", value: "中国" }, { label: "美国", value: "美国" },
  { label: "英国", value: "英国" }, { label: "日本", value: "日本" }, { label: "韩国", value: "韩国" },
];

const yearTags = [
  { label: "全部", value: null }, { label: "2024", value: "2024" }, { label: "2023", value: "2023" },
  { label: "2022", value: "2022" }, { label: "2010年代", value: "2010s" }, { label: "2000年代", value: "2000s" },
];

const filterParams = ref({
  genre: null, country: null, releaseYear: null,
  sortBy: "averageRating", sortDir: "desc",
  yearStart: null, yearEnd: null,
});

const quickFilter = ref("hot");
const pagination = ref({ page: 1, pageSize: 20, pageCount: 1 });

const fetchData = async () => {
  loading.value = true;
  // 在准备发送给后端的参数时，我们不需要 releaseYear，只需要 yearStart/yearEnd
  // 加一行elint忽略releaseYear报错
  // eslint-disable-next-line no-unused-vars
  const { releaseYear, ...apiParams } = filterParams.value;
  const params = {
    ...Object.fromEntries(
      Object.entries(apiParams).filter(([, v]) => v !== null && v !== "")
    ),
    page: pagination.value.page - 1,
    size: pagination.value.pageSize,
  };

  try {
    const response = await apiService.getLatestMovies(params);
    movies.value = response.data.content;
    pagination.value.pageCount = response.data.totalPages;
  } catch (error) {
    console.error("获取电影数据失败:", error);
  } finally {
    loading.value = false;
  }
};

const selectFilter = (key, value) => {
  const newQuery = { ...route.query };
  delete newQuery.quickFilter;
  delete newQuery.page;

  delete newQuery.releaseYear;
  delete newQuery.yearStart;
  delete newQuery.yearEnd;

  // 如果点击的是当前已选中的，则取消选择
  if (filterParams.value[key] === value) {
    // 这部分逻辑对于年份/年代有点特殊，但对于类型/国家是正确的
    // 暂定：点击已选中的标签就取消它
    // 此处我们依赖 watcher 来同步状态，所以直接清空相关 query
  } else if (key === 'releaseYear') {
    if (value === '2010s') {
      newQuery.yearStart = 2010;
      newQuery.yearEnd = 2019;
    } else if (value === '2000s') {
      newQuery.yearStart = 2000;
      newQuery.yearEnd = 2009;
    } else if (value) {
      newQuery.releaseYear = value;
    }
  } else {
    newQuery[key] = value;
  }
  router.push({ query: newQuery });
};

const handleQuickFilter = (type) => { router.push({ query: { quickFilter: type } }); };
const handlePageChange = (currentPage) => { router.push({ query: { ...route.query, page: currentPage } }); };

watch(() => route.query, (query) => {
  pagination.value.page = query.page ? Number(query.page) : 1;
  quickFilter.value = query.quickFilter || "";

  // 更新筛选参数
  filterParams.value.genre = query.genre || null;
  filterParams.value.country = query.country || null;
  filterParams.value.releaseYear = query.releaseYear || null;
  filterParams.value.yearStart = query.yearStart || null;
  filterParams.value.yearEnd = query.yearEnd || null;

  // 根据URL参数反向设置高亮状态
  if (!filterParams.value.releaseYear) {
    if (filterParams.value.yearStart == '2010' && filterParams.value.yearEnd == '2019') {
      filterParams.value.releaseYear = '2010s';
    } else if (filterParams.value.yearStart == '2000' && filterParams.value.yearEnd == '2009') {
      filterParams.value.releaseYear = '2000s';
    }
  }

  // 根据状态设置排序规则
  if (quickFilter.value === "all") {
    filterParams.value.sortBy = "title";
    filterParams.value.sortDir = "asc";
  } else if (quickFilter.value === "latest") {
    filterParams.value.sortBy = "releaseYear";
    filterParams.value.sortDir = "desc";
  } else {
    filterParams.value.sortBy = "averageRating";
    filterParams.value.sortDir = "desc";
  }

  if (Object.keys(query).length === 0) { quickFilter.value = "hot"; }
  fetchData();
}, { immediate: true, deep: true, }
);
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

.n-button[text] {
  font-size: 16px;
  margin: 0 8px;
}
</style>