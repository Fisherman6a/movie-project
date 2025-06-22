<script setup>
import { h } from 'vue';
import { RouterLink } from 'vue-router';
import { useAuthStore } from '@/store/auth';
import { NSpace, NButton, NAvatar, NDropdown, NText } from 'naive-ui'; // 1. 导入 NText

const authStore = useAuthStore();

const options = [
  {
    label: () => h(RouterLink, { to: { name: 'profile' } }, { default: () => '个人中心' }),
    key: 'profile'
  },
  {
    label: '退出登录',
    key: 'logout'
  }
];

const handleSelect = (key) => {
  if (key === 'logout') {
    authStore.logout();
  }
};
</script>

<template>
  <div class="header-wrapper">
    <RouterLink :to="{ name: 'home' }" style="text-decoration: none;">
      <n-text type="primary" :strong="true" style="font-size: 24px; color: #f0a020;">
        影评系统
      </n-text>
    </RouterLink>
    
    <n-space align="center" :size="20">
      <RouterLink :to="{ name: 'movies' }">
        <n-button text style="color: white; font-size: 16px;">发现电影</n-button>
      </RouterLink>

      <div v-if="authStore.isAuthenticated">
        <n-space align="center">
          <RouterLink v-if="authStore.isAdmin" :to="{ name: 'admin-dashboard' }">
            <n-button text style="color: white;">管理后台</n-button>
          </RouterLink>
          <n-dropdown trigger="hover" :options="options" @select="handleSelect">
            <n-avatar round size="small" style="cursor: pointer; background-color: #f0a020;">
              {{ authStore.user?.username?.[0].toUpperCase() }}
            </n-avatar>
          </n-dropdown>
        </n-space>
      </div>
      <div v-else>
        <n-space>
          <RouterLink :to="{ name: 'login' }">
            <n-button secondary>登录</n-button>
          </RouterLink>
          <RouterLink :to="{ name: 'register' }">
            <n-button type="primary">注册</n-button>
          </RouterLink>
        </n-space>
      </div>
    </n-space>
  </div>
</template>

<style scoped>
/* 4. 添加样式来控制布局 */
.header-wrapper {
  height: 64px; /* 确保高度 */
  display: flex;
  justify-content: space-between; /* 两端对齐 */
  align-items: center; /* 垂直居中 */
  max-width: 1280px; /* 保持和内容区一样的宽度 */
  margin: 0 auto; /* 居中 */
}
</style>