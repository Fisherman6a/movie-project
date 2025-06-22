<template>
    <div class="max-w-2xl mx-auto">
      <n-card title="个人中心" :bordered="false" style="background-color: #242424;">
        <div v-if="loading">加载中...</div>
        <div v-if="authStore.user">
          <n-descriptions label-placement="left" title="用户信息" :column="1">
            <n-descriptions-item label="用户ID">
              {{ authStore.userId }}
            </n-descriptions-item>
            <n-descriptions-item label="用户名">
              {{ authStore.user.username }}
            </n-descriptions-item>
            <n-descriptions-item label="角色">
              {{ authStore.isAdmin ? '管理员' : '普通用户' }}
            </n-descriptions-item>
          </n-descriptions>
        </div>
         <n-empty v-else description="无法加载用户信息" />
      </n-card>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { useAuthStore } from '@/store/auth';
  import { NCard, NDescriptions, NDescriptionsItem, NEmpty } from 'naive-ui';
  
  const authStore = useAuthStore();
  const loading = ref(false);
  
  // Pinia state is reactive, so we can directly use it in the template.
  // If you needed to fetch more data, you would do it in onMounted.
  </script>