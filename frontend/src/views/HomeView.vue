<script setup>
import { ref, onMounted } from 'vue';
// 1. 导入新增的 API 方法
import { getHotMovies, getLatestMovies } from '@/api/movies';
import MovieList from '@/components/movie/MovieList.vue';
import { NSpin, NH1, NP, NText, NDivider } from 'naive-ui';
import { useMessage } from 'naive-ui';

const message = useMessage();

// 为“热门推荐”和“最新电影”分别创建数据和加载状态
const hotMovies = ref([]);
const isHotLoading = ref(true);
const latestMovies = ref([]);
const isLatestLoading = ref(true);

// 2. 在 onMounted 中同时获取两种数据
onMounted(async () => {
  try {
    // 使用 Promise.all 并发请求，提升加载速度
    const [hotResponse, latestResponse] = await Promise.all([
      getHotMovies(6),      // 获取6部热门电影
      getLatestMovies(6)    // 获取6部最新电影
    ]);
    hotMovies.value = hotResponse.data;
    latestMovies.value = latestResponse.data.content; // 分页接口的数据在 content 字段里
  } catch (error) {
    message.error("加载电影数据失败");
    console.error("加载电影数据失败:", error);
  } finally {
    isHotLoading.value = false;
    isLatestLoading.value = false;
  }
});
</script>

<template>
  <div>
    <section class="text-center mb-12">
      <n-h1>
        <n-text type="primary">探索电影世界</n-text>
      </n-h1>
      <n-p depth="3">发现、评论、分享你喜爱的电影</n-p>
    </section>

    <section>
      <n-h1 class="mb-6">热门推荐</n-h1>
      <n-spin :show="isHotLoading">
        <div v-if="hotMovies.length > 0">
          <MovieList :movies="hotMovies" />
        </div>
        <div v-else class="text-center py-16 text-gray-500">
          热门电影加载失败或暂无数据。
        </div>
      </n-spin>
    </section>

    <n-divider style="margin-top: 3rem; margin-bottom: 2rem;" />

    <section>
      <n-h1 class="mb-6">最新电影</n-h1>
      <n-spin :show="isLatestLoading">
        <div v-if="latestMovies.length > 0">
          <MovieList :movies="latestMovies" />
        </div>
        <div v-else class="text-center py-16 text-gray-500">
          最新电影加载失败或暂无数据。
        </div>
      </n-spin>
    </section>

  </div>
</template>

<style scoped>
.text-center {
  text-align: center;
}
.mb-12 {
  margin-bottom: 3rem;
}
.mb-6 {
  margin-bottom: 1.5rem;
}
.py-16 {
  padding-top: 4rem;
  padding-bottom: 4rem;
}
</style>