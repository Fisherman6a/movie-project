<template>
  <n-layout-content content-style="padding: 24px;">
    <n-h2 prefix="bar">
      <n-text type="primary">选电影</n-text>
    </n-h2>

    <n-space align="center" style="margin-bottom: 20px">
      <n-button
        text
        :type="quickFilter === 'all' ? 'primary' : 'default'"
        @click="handleQuickFilter('all')"
        tag="a"
        >全部</n-button
      >
      <n-divider vertical />
      <n-button
        text
        :type="quickFilter === 'hot' ? 'primary' : 'default'"
        @click="handleQuickFilter('hot')"
        tag="a"
        >热门电影</n-button
      >
      <n-button
        text
        :type="quickFilter === 'latest' ? 'primary' : 'default'"
        @click="handleQuickFilter('latest')"
        tag="a"
        >最新电影</n-button
      >
      <n-button
        text
        :type="quickFilter === 'top_rated' ? 'primary' : 'default'"
        @click="handleQuickFilter('top_rated')"
        tag="a"
        >高分佳片</n-button
      >
    </n-space>

    <n-card size="small">
      <n-space vertical :size="20">
        <n-space align="center">
          <n-text style="width: 50px">类型:</n-text>
          <n-space>
            <n-tag
              v-for="tag in genreTags"
              :key="tag.value"
              checkable
              :checked="filterParams.genre === tag.value"
              @click="selectFilter('genre', tag.value)"
            >
              {{ tag.label }}
            </n-tag>
          </n-space>
        </n-space>
        <n-space align="center">
          <n-text style="width: 50px">地区:</n-text>
          <n-space>
            <n-tag
              v-for="tag in countryTags"
              :key="tag.value"
              checkable
              :checked="filterParams.country === tag.value"
              @click="selectFilter('country', tag.value)"
            >
              {{ tag.label }}
            </n-tag>
          </n-space>
        </n-space>
        <n-space align="center">
          <n-text style="width: 50px">年代:</n-text>
          <n-space>
            <n-tag
              v-for="tag in yearTags"
              :key="tag.value"
              checkable
              :checked="filterParams.releaseYear === tag.value"
              @click="selectFilter('releaseYear', tag.value)"
            >
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
      <n-grid
        v-if="movies.length > 0"
        :x-gap="16"
        :y-gap="24"
        :cols="'2 s:3 m:4 l:5 xl:6'"
        responsive="true"
      >
        <n-grid-item v-for="movie in movies" :key="movie.id">
          <router-link :to="{ name: 'MovieDetail', params: { id: movie.id } }">
            <n-card :title="movie.title" hoverable content-style="padding:0;">
              <template #cover>
                <img :src="movie.posterUrl" class="movie-poster-img" />
              </template>
              <template #footer>
                <n-rate
                  readonly
                  :value="movie.averageRating / 2"
                  allow-half
                  size="small"
                />
              </template>
            </n-card>
          </router-link>
        </n-grid-item>
      </n-grid>
      <n-empty
        v-else
        description="没有找到符合条件的电影。"
        style="padding: 48px 0"
      />
    </n-spin>

    <n-flex justify="center" style="margin-top: 24px">
      <n-pagination
        v-model:page="pagination.page"
        :page-count="pagination.pageCount"
        @update:page="handlePageChange"
      />
    </n-flex>
  </n-layout-content>
</template>

<script setup>
import { ref, watch, onMounted } from "vue";
import { useRoute } from "vue-router";
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
const loading = ref(true);
const movies = ref([]);

// --- 筛选数据模型 ---

// 筛选标签数据
const genreTags = [
  { label: "全部", value: null },
  { label: "剧情", value: "剧情" },
  { label: "喜剧", value: "喜剧" },
  { label: "科幻", value: "科幻" },
  { label: "动作", value: "动作" },
  { label: "爱情", value: "爱情" },
  { label: "动画", value: "动画" },
  { label: "悬疑", value: "悬疑" },
  { label: "惊悚", value: "惊悚" },
];
const countryTags = [
  { label: "全部", value: null },
  { label: "中国大陆", value: "中国大陆" },
  { label: "美国", value: "美国" },
  { label: "日本", value: "日本" },
  { label: "韩国", value: "韩国" },
  { label: "英国", value: "英国" },
];
const yearTags = [
  { label: "全部", value: null },
  { label: "2024", value: 2024 },
  { label: "2023", value: 2023 },
  { label: "2022", value: 2022 },
  { label: "2010年代", value: "2010s" },
  { label: "2000年代", value: "2000s" },
];

// 筛选参数
const filterParams = ref({
  genre: null,
  country: null,
  releaseYear: null,
  sortBy: "averageRating", // 默认按热门排序
  sortDir: "desc",
});

// 顶部快速筛选状态
const quickFilter = ref("hot");

// 分页参数
const pagination = ref({
  page: 1,
  pageSize: 20,
  pageCount: 1,
});

// --- 方法 ---

// 核心数据获取函数
const fetchData = async () => {
  loading.value = true;
  const params = {
    ...Object.fromEntries(
      Object.entries(filterParams.value).filter(
        ([, v]) => v !== null && v !== ""
      )
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

// 点击标签进行筛选
const selectFilter = (key, value) => {
  // 如果点击的是已选中的标签，则取消选择（等同于点击“全部”）
  if (filterParams.value[key] === value) {
    filterParams.value[key] = null;
  } else {
    filterParams.value[key] = value;
  }
  // 手动筛选时，清除快速筛选的状态
  quickFilter.value = "";
  pagination.value.page = 1;
  fetchData();
};

// 点击顶部快速筛选
const handleQuickFilter = (type) => {
  quickFilter.value = type;
  // 重置详细筛选
  filterParams.value.genre = null;
  filterParams.value.country = null;
  filterParams.value.releaseYear = null;

  if (type === "all") {
    filterParams.value.sortBy = "title";
    filterParams.value.sortDir = "asc";
  } else if (type === "hot") {
    filterParams.value.sortBy = "averageRating";
    filterParams.value.sortDir = "desc";
  } else if (type === "latest") {
    filterParams.value.sortBy = "releaseYear";
    filterParams.value.sortDir = "desc";
  } else if (type === "top_rated") {
    filterParams.value.sortBy = "averageRating";
    filterParams.value.sortDir = "desc";
    // 可以在这里加上评分下限，例如 filterParams.value.minRating = 8;
  }
  pagination.value.page = 1;
  fetchData();
};

// 处理分页变化
const handlePageChange = (currentPage) => {
  pagination.value.page = currentPage;
  fetchData();
};

// 监听从 Header 搜索跳转过来的 query
watch(
  () => route.query.title,
  (newTitle) => {
    if (newTitle) {
      // 这里可以添加一个 title 的筛选参数，如果需要的话
      console.log("需要按标题搜索:", newTitle);
    }
  }
);

// 页面加载时获取初始数据
onMounted(() => {
  fetchData();
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
.n-button[text] {
  font-size: 16px;
  margin: 0 8px;
}
</style>
