<template>
  <n-layout-content content-style="padding: 24px;">
    <n-h2>我的消息</n-h2>

    <!-- 操作按钮 -->
    <n-space style="margin-bottom: 16px;">
      <n-button type="primary" ghost @click="handleMarkAllAsRead" :disabled="unreadCount === 0">
        全部标记为已读
      </n-button>
      <n-button type="error" ghost @click="handleDeleteAllRead" :disabled="readCount === 0">
        删除所有已读消息
      </n-button>
      <n-badge :value="unreadCount" :max="99" show-zero>
        <n-button quaternary>未读消息</n-button>
      </n-badge>
    </n-space>

    <n-spin :show="loading">
      <n-list bordered>
        <n-list-item v-for="notification in mergedNotifications" :key="notification.id || notification.mergedIds?.[0]">
          <n-thing>
            <template #header>
              <n-space align="center">
                <!-- 通知标题 -->
                <n-tag :type="getNotificationTagType(notification.type)" size="small">
                  {{ notification.title }}
                </n-tag>
                <span>{{ notification.message }}</span>
                <!-- 未读标记 -->
                <n-badge v-if="!notification.isRead" dot processing />
              </n-space>
            </template>

            <template #header-extra>
              <n-space>
                <n-button
                  v-if="!notification.isRead"
                  size="small"
                  type="info"
                  ghost
                  @click="handleMarkAsRead(notification)"
                >
                  标记已读
                </n-button>
                <n-popconfirm @positive-click="handleDelete(notification)">
                  <template #trigger>
                    <n-button size="small" type="error" ghost>删除</n-button>
                  </template>
                  {{ notification.isMerged ? `确定要删除这 ${notification.mergeCount} 条消息吗？` : '确定要删除这条消息吗？' }}
                </n-popconfirm>
              </n-space>
            </template>

            <template #description>
              <n-space align="center">
                <n-icon v-if="notification.type === 'LIKE'" :component="ThumbsUpOutline" />
                <n-icon v-if="notification.type === 'COMMENT'" :component="ChatbubbleOutline" />
                <n-icon v-if="notification.type === 'SYSTEM'" :component="NotificationsOutline" />

                <n-text depth="3">
                  {{ formatTime(notification.createdAt) }}
                </n-text>

                <n-text v-if="notification.isRead && notification.readAt" depth="3">
                  · 已读于 {{ formatTime(notification.readAt) }}
                </n-text>

                <n-text v-if="notification.triggerUsername" depth="2">
                  · 来自 {{ notification.triggerUsername }}
                </n-text>
              </n-space>
            </template>

            <!-- 可选：点击跳转到相关内容 -->
            <div v-if="notification.relatedType === 'REVIEW' && notification.relatedId">
              <n-button
                text
                type="primary"
                size="small"
                @click="goToReview(notification.relatedId)"
              >
                查看相关评论 →
              </n-button>
            </div>
          </n-thing>
        </n-list-item>

        <n-list-item v-if="mergedNotifications.length === 0">
          <n-empty description="您还没有收到任何消息。" />
        </n-list-item>
      </n-list>
    </n-spin>
  </n-layout-content>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';
import apiService from '@/services/apiService';
import {
  NLayoutContent, NH2, NList, NListItem, NThing, NButton, NSpace,
  NPopconfirm, NSpin, NEmpty, useMessage, NText, NTag, NBadge, NIcon
} from 'naive-ui';
import { ThumbsUpOutline, ChatbubbleOutline, NotificationsOutline } from '@vicons/ionicons5';

const authStore = useAuthStore();
const message = useMessage();
const router = useRouter(); // 启用路由
const notifications = ref([]);
const loading = ref(true);

// 合并后的通知列表（用于展示）
const mergedNotifications = computed(() => {
  const merged = [];
  const groupMap = new Map(); // key: "triggerUserId-relatedType-relatedId", value: 通知数组

  // 1. 按照 "谁对什么内容的操作" 进行分组
  notifications.value.forEach(notification => {
    // 只合并点赞类型的通知
    if (notification.type === 'LIKE' && notification.triggerUserId && notification.relatedId) {
      const key = `${notification.triggerUserId}-${notification.relatedType}-${notification.relatedId}`;
      if (!groupMap.has(key)) {
        groupMap.set(key, []);
      }
      groupMap.get(key).push(notification);
    } else {
      // 其他类型通知不合并，直接加入结果
      merged.push(notification);
    }
  });

  // 2. 处理合并后的点赞通知
  groupMap.forEach((group) => {
    if (group.length === 1) {
      // 只有一条，不需要合并
      merged.push(group[0]);
    } else {
      // 多条，合并为一条
      const firstNotification = group[0];
      const mergedNotification = {
        ...firstNotification,
        // 使用最新的通知时间
        createdAt: group[group.length - 1].createdAt,
        // 标记为合并通知
        isMerged: true,
        mergeCount: group.length,
        // 是否已读：只有全部都已读才算已读
        isRead: group.every(n => n.isRead),
        // 保存所有通知的ID，用于批量标记已读或删除
        mergedIds: group.map(n => n.id),
        // 修改消息内容，添加次数标注
        message: `${firstNotification.message} ×${group.length}`
      };
      merged.push(mergedNotification);
    }
  });

  // 3. 按时间倒序排列
  return merged.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
});

// 计算未读和已读数量（基于原始通知列表）
const unreadCount = computed(() => notifications.value.filter(n => !n.isRead).length);
const readCount = computed(() => notifications.value.filter(n => n.isRead).length);

// 获取通知列表
const fetchNotifications = async () => {
  loading.value = true;
  try {
    const response = await apiService.getUserNotifications(authStore.userId);
    notifications.value = response.data;
  } catch (error) {
    message.error('加载消息失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

// 标记单条消息为已读（支持合并通知）
const handleMarkAsRead = async (notification) => {
  try {
    // 如果是合并的通知，需要标记所有相关的通知为已读
    const idsToMark = notification.isMerged ? notification.mergedIds : [notification.id];

    await Promise.all(idsToMark.map(id => apiService.markNotificationAsRead(id)));

    // 更新本地状态
    idsToMark.forEach(id => {
      const n = notifications.value.find(item => item.id === id);
      if (n) {
        n.isRead = true;
        n.readAt = new Date().toISOString();
      }
    });

    message.success(notification.isMerged ? `已标记 ${notification.mergeCount} 条消息为已读` : '已标记为已读');
  } catch (error) {
    message.error('操作失败');
  }
};

// 全部标记为已读
const handleMarkAllAsRead = async () => {
  try {
    await apiService.markAllNotificationsAsRead(authStore.userId);
    // 更新本地状态
    notifications.value.forEach(n => {
      if (!n.isRead) {
        n.isRead = true;
        n.readAt = new Date().toISOString();
      }
    });
    message.success('所有消息已标记为已读');
  } catch (error) {
    message.error('操作失败');
  }
};

// 删除单条消息（支持合并通知）
const handleDelete = async (notification) => {
  try {
    // 如果是合并的通知，需要删除所有相关的通知
    const idsToDelete = notification.isMerged ? notification.mergedIds : [notification.id];

    await Promise.all(idsToDelete.map(id => apiService.deleteNotification(id)));

    // 更新本地状态
    notifications.value = notifications.value.filter(n => !idsToDelete.includes(n.id));

    message.success(notification.isMerged ? `已删除 ${notification.mergeCount} 条消息` : '删除成功');
  } catch (error) {
    message.error('删除失败');
  }
};

// 删除所有已读消息
const handleDeleteAllRead = async () => {
  try {
    await apiService.deleteAllReadNotifications(authStore.userId);
    notifications.value = notifications.value.filter(n => !n.isRead);
    message.success('已删除所有已读消息');
  } catch (error) {
    message.error('操作失败');
  }
};

// 跳转到相关评论
const goToReview = async (reviewId) => {
  try {
    // 1. 通过 reviewId 获取评论详情（包含 movieId）
    const response = await apiService.getAllReviews();
    const review = response.data.find(r => r.id === reviewId);

    if (!review) {
      message.warning('找不到相关评论');
      return;
    }

    // 2. 跳转到电影详情页，hash 定位到评论区
    // 注意：路径是 /movies/:id (复数)
    router.push({
      path: `/movies/${review.movieId}`,
      hash: `#review-${reviewId}`
    });

    message.success('正在跳转...');
  } catch (error) {
    console.error('跳转失败:', error);
    message.error('跳转失败，请稍后重试');
  }
};

// 根据通知类型返回标签样式
const getNotificationTagType = (type) => {
  switch (type) {
    case 'LIKE':
      return 'success';
    case 'COMMENT':
      return 'info';
    case 'SYSTEM':
      return 'warning';
    default:
      return 'default';
  }
};

// 格式化时间
const formatTime = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  const now = new Date();
  const diff = now - date;
  const minutes = Math.floor(diff / 60000);
  const hours = Math.floor(diff / 3600000);
  const days = Math.floor(diff / 86400000);

  if (minutes < 1) return '刚刚';
  if (minutes < 60) return `${minutes}分钟前`;
  if (hours < 24) return `${hours}小时前`;
  if (days < 7) return `${days}天前`;

  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

onMounted(() => {
  if (authStore.isAuthenticated) {
    fetchNotifications();
  }
});
</script>

<style scoped>
/* 可以添加一些自定义样式 */
</style>
