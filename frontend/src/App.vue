<template>
  <div class="app-container">
    <n-config-provider :theme="darkTheme" :theme-overrides="themeOverrides">
      <n-message-provider>
        <n-dialog-provider>

          <n-layout-header bordered class="app-header">
            <AppHeader />
          </n-layout-header>

          <n-layout-content class="app-content">
            <div class="main-content-wrapper">
              <router-view />
            </div>
          </n-layout-content>

          <n-layout-footer bordered class="app-footer">
            <AppFooter />
          </n-layout-footer>

        </n-dialog-provider>
      </n-message-provider>
    </n-config-provider>
  </div>
</template>

<script setup>
// script 部分无需改动
import { darkTheme, NConfigProvider, NMessageProvider, NDialogProvider, NLayoutHeader, NLayoutContent, NLayoutFooter } from 'naive-ui';
import AppHeader from './components/layout/AppHeader.vue';
import AppFooter from './components/layout/AppFooter.vue';

const themeOverrides = {
  common: {
    primaryColor: '#f0a020',
    primaryColorHover: '#f0a020d1',
    primaryColorPressed: '#e09010',
    primaryColorSuppl: '#f0a020'
  }
};
</script>

<style>
/* --- 这里是解决所有布局问题的关键 --- */
html, body, #app {
  height: 100%;
  margin: 0;
  padding: 0;
}

.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100%;
}

.app-header {
  flex-shrink: 0; /* 防止页头被压缩 */
}

.app-content {
  flex-grow: 1; /* 让内容区自动伸展，填满所有剩余空间 */
  padding: 200px;
  background-color: #1a1a1a; /* 为内容区指定背景色 */
}

.app-footer {
  flex-shrink: 1; /* 防止页脚被压缩 */
  height: 60px; /* ✨ 根据您的要求，设定一个固定的高度 */
  display: flex;
  align-items: center;
  justify-content: center;
}

.main-content-wrapper {
  max-width: 1280px;
  margin: 0 auto;
}
</style>