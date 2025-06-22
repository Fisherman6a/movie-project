<template>
    <div>
        <h1 class="text-2xl font-bold mb-4">电影管理</h1>
        <p>这里将是电影管理的数据表格。功能待实现。</p>
        <n-data-table
            :columns="columns"
            :data="movies"
            :pagination="pagination"
            :loading="loading"
        />
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { NDataTable } from 'naive-ui';
import { searchMovies } from '@/api/movies';

const loading = ref(true);
const movies = ref([]);

const columns = [
    { title: 'ID', key: 'id' },
    { title: '电影名称', key: 'title' },
    { title: '发行年份', key: 'releaseYear' },
    { title: '评分', key: 'averageRating' },
];

const pagination = ref({ pageSize: 10 });

onMounted(async () => {
    try {
        const response = await searchMovies({ page: 0, size: 10 });
        movies.value = response.data.content;
    } catch(err) {
        console.error("获取电影数据失败", err);
    } finally {
        loading.value = false;
    }
});
</script>