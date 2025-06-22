<script setup>
import { computed } from 'vue';
import { NCard, NRate } from 'naive-ui';
import { RouterLink } from 'vue-router';

const props = defineProps({
    movie: {
        type: Object,
        required: true
    }
});

const posterUrl = computed(() => props.movie.posterUrl || 'https://via.placeholder.com/400x600.png?text=No+Image');
</script>

<template>
    <RouterLink :to="{ name: 'movie-detail', params: { id: movie.id } }">
        <n-card hoverable class="movie-card" :body-style="{ padding: 0 }" :content-style="{ padding: '16px' }">
            <template #cover>
                <img :src="posterUrl" :alt="movie.title" class="aspect-[2/3] object-cover">
            </template>
            <h3 class="text-white font-semibold truncate">{{ movie.title }}</h3>
            <div class="flex items-center mt-2">
                <n-rate readonly :default-value="movie.averageRating / 2" size="small" allow-half />
                <span class="ml-2 text-gray-400 text-sm">{{ movie.averageRating?.toFixed(1) || 'N/A' }}</span>
            </div>
        </n-card>
    </RouterLink>
</template>

<style scoped>
.movie-card {
    background-color: #2a2a2a;
    border: 1px solid #3a3a3a;
    transition: all 0.3s ease;
}

.movie-card:hover {
    transform: translateY(-5px);
    border-color: #f0a020;
}

img {
    width: 100%;
}
</style>