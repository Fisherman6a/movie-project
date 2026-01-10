<template>
  <div style="display: none;">
    <!-- 该组件不渲染任何可见内容，仅处理 WebSocket 通知逻辑 -->
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, watch } from 'vue';
import { useMessage } from 'naive-ui';
import { useAuthStore } from '@/stores/authStore';
import websocketService from '@/services/websocketService';

const message = useMessage();
const authStore = useAuthStore();

// 处理接收到的通知
const handleNotification = (notification) => {
  console.log('处理通知:', notification);

  // 根据通知类型显示不同的消息
  switch (notification.type) {
    case 'LIKE':
      message.info(notification.message, {
        duration: 5000,
        closable: true,
      });
      break;
    case 'COMMENT':
      message.info(notification.message, {
        duration: 5000,
        closable: true,
      });
      break;
    case 'SYSTEM':
      message.warning(notification.message, {
        duration: 5000,
        closable: true,
      });
      break;
    default:
      message.info(notification.message, {
        duration: 5000,
        closable: true,
      });
  }

  // 可以在这里添加更多逻辑，比如：
  // - 更新未读通知数量
  // - 播放提示音
  // - 显示桌面通知
};

onMounted(() => {
  // 如果用户已登录，建立 WebSocket 连接
  if (authStore.isAuthenticated && authStore.userId) {
    console.log('用户已登录，建立 WebSocket 连接, userId:', authStore.userId);
    websocketService.connect(authStore.userId, handleNotification);
  }
});

onUnmounted(() => {
  // 组件销毁时断开 WebSocket 连接
  websocketService.disconnect();
});

// 监听用户登录/登出状态变化，动态管理 WebSocket 连接
watch(() => authStore.isAuthenticated, (newValue) => {
  if (newValue && authStore.userId) {
    // 用户登录，建立连接
    console.log('用户登录，建立 WebSocket 连接');
    websocketService.connect(authStore.userId, handleNotification);
  } else {
    // 用户登出，断开连接
    console.log('用户登出，断开 WebSocket 连接');
    websocketService.disconnect();
  }
});
</script>
