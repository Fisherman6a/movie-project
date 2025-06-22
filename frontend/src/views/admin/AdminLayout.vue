<template>
    <n-layout has-sider style="height: calc(100vh - 64px)">
      <n-layout-sider
        bordered
        collapse-mode="width"
        :collapsed-width="64"
        :width="240"
        show-trigger
        style="background-color: #242424;"
      >
        <n-menu
          v-model:value="activeKey"
          :options="menuOptions"
          style="background-color: #242424;"
        />
      </n-layout-sider>
      <n-layout-content style="padding: 24px;">
        <router-view />
      </n-layout-content>
    </n-layout>
</template>

<script setup>
import { h, ref } from 'vue';
import { RouterLink } from 'vue-router';
import { NLayout, NLayoutSider, NLayoutContent, NMenu } from 'naive-ui';

const activeKey = ref(null);

const menuOptions = [
  {
    label: () => h(RouterLink, { to: { name: 'admin-dashboard' } }, { default: () => '仪表盘' }),
    key: 'admin-dashboard',
  },
  {
    label: () => h(RouterLink, { to: { name: 'admin-movies' } }, { default: () => '电影管理' }),
    key: 'admin-movies',
  },
  // 你可以继续添加演员和导演管理
];
</script>