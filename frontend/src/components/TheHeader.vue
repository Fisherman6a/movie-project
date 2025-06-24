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

      <n-space align="center">
        <router-link to="/login" v-if="!authStore.isAuthenticated">
          <n-avatar round>
            登录
          </n-avatar>
        </router-link>

        <!-- 使用计算属性 dropdownOptions -->
        <n-dropdown v-else :options="dropdownOptions" @select="handleDropdownSelect">
          <n-avatar round :src="authStore.profileImageUrl" />
        </n-dropdown>
      </n-space>
    </n-flex>
  </n-layout-header>
  <div style="margin-top: 20px;">
  </div>
</template>

<script setup>
// ========== START: Import 'computed' ==========
import { ref, computed } from 'vue';
// ========== END: Import 'computed' ==========
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';
import { useSearchStore } from '@/stores/searchStore';
import { NLayoutHeader, NFlex, NText, NInputGroup, NInput, NButton, NSpace, NAvatar, NDropdown, useMessage } from 'naive-ui';

const router = useRouter();
const message = useMessage();
const authStore = useAuthStore();
const searchTerm = ref('');
const searchStore = useSearchStore();

// ========== START: Convert dropdownOptions to a computed property ==========
const dropdownOptions = computed(() => {
  const options = [
    {
      label: '我的主页',
      key: 'profile'
    },
    {
      label: '我的评论',
      key: 'my-reviews'
    },
    {
      label: '账号设置',
      key: 'settings'
    },
    {
      type: 'divider',
      key: 'd1'
    }
  ];

  // 如果是管理员，添加管理后台入口
  if (authStore.user && authStore.user.role === 'ROLE_ADMIN') {
    options.push({
      label: '管理后台',
      key: 'admin-dashboard'
    });
  }

  options.push({
    label: '登出',
    key: 'logout'
  });

  return options;
});
// ========== END: Convert dropdownOptions to a computed property ==========


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

const handleDropdownSelect = (key) => {
  switch (key) {
    case 'profile':
      router.push('/profile');
      break;
    case 'settings':
      router.push('/settings');
      break;
    case 'my-reviews':
      router.push('/my-reviews');
      break;
    case 'admin-dashboard':
      router.push('/admin');
      break;
    case 'logout':
      handleLogout();
      break;
  }
};
</script>

<style scoped>
a {
  text-decoration: none;
}

.n-avatar {
  cursor: pointer;
}
</style>