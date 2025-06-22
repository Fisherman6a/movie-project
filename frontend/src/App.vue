// src/App.vue
<template>
  <n-config-provider>
    <n-message-provider>
      <n-dialog-provider>
        <router-view />
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup>
// 从 naive-ui 导入需要在模板中使用的全局配置型组件
import { NConfigProvider, NMessageProvider, NDialogProvider } from 'naive-ui';

// 注意：App.vue 通常不需要导入具体的页面组件（比如 HomeView.vue），
// 因为这些是由 Vue Router 根据路由配置动态加载到 <router-view /> 中的。
</script>

<style>
/* 在这里可以放置一些全局的 CSS 样式 */
/* 例如，重置默认边距或设置基础字体 */
body {
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial,
    'Noto Sans', sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol',
    'Noto Color Emoji'; /* 一个更现代的字体栈示例 */
  -webkit-font-smoothing: antialiased; /* 抗锯齿，使字体更平滑 (针对 WebKit 内核浏览器) */
  -moz-osx-font-smoothing: grayscale; /* 抗锯齿 (针对 Firefox) */
}

/* 你也可以在这里引入一个全局的 CSS 重置文件，比如 normalize.css 或 reset.css */
/* @import './assets/css/normalize.css'; */

/* 如果你的 vfonts 没有在 main.js 全局引入 Lato.css 和 FiraCode.css，
   或者你想确保 App.vue 使用特定字体，也可以在这里引入。
   但通常建议在 main.js 中引入 vfonts，以便全局生效。
*/
/*
@import 'vfonts/Lato.css';
@import 'vfonts/FiraCode.css';
*/
</style>