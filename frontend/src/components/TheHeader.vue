<template>
  <n-layout-header bordered style="padding: 12px 24px;">
    <n-flex justify="space-between" align="center">
      <router-link to="/">
        <n-text tag="div" style="font-size: 24px; font-weight: bold; cursor: pointer;">
          🎬 影评时光
        </n-text>
      </router-link>

      <n-input-group style="max-width: 400px;">
        <n-input v-model:value="searchTerm" :style="{ width: '80%' }" placeholder="搜索电影、演员、导演..."
          @keyup.enter="handleSearch" :loading="searchStore.isLoading" />
        <n-button type="primary" ghost @click="handleSearch">
          搜索
        </n-button>
      </n-input-group>

      <n-space v-if="!authStore.isAuthenticated">
        <router-link to="/login"><n-button text strong secondary type="primary">登录</n-button></router-link>
      </n-space>
      <n-space v-else>
        <n-text>欢迎, {{ authStore.username }}</n-text>
        <router-link to="/my-reviews"><n-button text>我的评论</n-button></router-link>
        <n-button text @click="handleLogout">登出</n-button>
      </n-space>
    </n-flex>
  </n-layout-header>
</template>
  
  <script setup>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { useAuthStore } from '@/stores/authStore';
  import { useSearchStore } from '@/stores/searchStore'; 
  import { NLayoutHeader, NFlex, NText, NInputGroup, NInput, NButton, NSpace, useMessage } from 'naive-ui';
  
  const router = useRouter();
  const message = useMessage();
  const authStore = useAuthStore();
  const searchTerm = ref('');
  const searchStore = useSearchStore(); 
  
  const handleSearch = () => {
    if (searchTerm.value.trim()) {
      router.push({ name: 'MovieSearch', query: { keyword: searchTerm.value.trim() } });
      searchTerm.value = '';
    }
  };
  
  const handleLogout = () => {
      authStore.clearAuth();
      message.success('已成功登出');
      router.push('/');
  };
  </script>
  
  <style scoped>
  a {
      text-decoration: none;
  }
  </style>