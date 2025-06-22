<template>
    <div>
      <h1 class="text-2xl font-bold mb-4">管理后台仪表盘</h1>
      <p>欢迎回来, {{ authStore.user?.username }}!</p>
      <p class="mt-4">请从左侧菜单中选择一项进行管理。</p>
    </div>
  </template>
  
  <script setup>
  import { useAuthStore } from '@/store/auth';
  const authStore = useAuthStore();
  </script>