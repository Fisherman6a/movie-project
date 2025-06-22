<template>
    <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-6">
      <MovieCard 
        v-for="movie in movies" 
        :key="movie.id" 
        :movie="movie" 
      />
    </div>
  </template>
  
  <script setup>
  import MovieCard from '@/components/movie/MovieCard.vue';
  
  defineProps({
    movies: {
      type: Array,
      required: true,
    },
  });
  </script>
  
  <style scoped>
  .grid {
    display: grid;
  }
  </style>