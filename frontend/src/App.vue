<template>
  <n-config-provider :theme="darkTheme">
    <n-message-provider>
      <n-dialog-provider>
        <n-layout style="min-height: 100vh;">
          <TheHeader />

          <div class="main-content-container">
            <router-view />
            <TheFooter />
          </div>

        </n-layout>
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup>
import { NConfigProvider, NMessageProvider, NDialogProvider, NLayout, darkTheme } from 'naive-ui';
import TheHeader from '@/components/TheHeader.vue';
import TheFooter from '@/components/TheFooter.vue';
</script>

<style>
body {
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial,
    'Noto Sans', sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol',
    'Noto Color Emoji';
}

/* 新增的容器样式 */
.main-content-container {
  /* 设置内容区域的最大宽度 */
  max-width: 1400px; 
  /* 使用 margin: 0 auto; 实现水平居中 */
  margin: 0 auto;
  /* 确保在小屏幕上也能正常显示，并有一定边距 */
  width: 100%;
  padding: 0 24px;
  box-sizing: border-box;
}
</style>