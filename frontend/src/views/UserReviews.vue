<template>
  <n-layout-content content-style="padding: 24px;">
    <n-h2>我的评论</n-h2>
    <n-spin :show="loading">
      <n-list bordered>
        <n-list-item v-for="review in myReviews" :key="review.id">
          <n-thing>
            <template #header>
              对电影《{{ review.movieTitle }}》的评论
            </template>
            <template #header-extra>
              <n-space>
                <n-button size="small" type="primary" ghost @click="openEditModal(review)">编辑</n-button>
                <n-popconfirm @positive-click="handleDelete(review.id)">
                  <template #trigger>
                    <n-button size="small" type="error" ghost>删除</n-button>
                  </template>
                  确定要删除这条评论吗？
                </n-popconfirm>
              </n-space>
            </template>
            <template #description>
              <n-space align="center">
                <n-rate readonly :value="review.score / 2" allow-half size="small" />
                <n-text depth="3">发表于: {{ new Date(review.createdAt).toLocaleString() }}</n-text>
              </n-space>
            </template>
            {{ review.commentText }}
          </n-thing>
        </n-list-item>
        <n-list-item v-if="myReviews.length === 0">
          <n-empty description="您还没有发表过任何评论。" />
        </n-list-item>
      </n-list>
    </n-spin>
  </n-layout-content>

  <n-modal v-model:show="showEditModal" preset="card" style="width: 600px" title="编辑评论">
    <n-space vertical>
      <n-rate v-model:value="editingScore" :count="5" size="large" allow-half />
      <n-input v-model:value="editingCommentText" type="textarea" placeholder="输入新的评论内容..."
        :autosize="{ minRows: 5 }" />
    </n-space>
    <template #footer>
      <n-flex justify="end">
        <n-button @click="showEditModal = false">取消</n-button>
        <n-button type="primary" @click="handleUpdate" :loading="isUpdating">保存修改</n-button>
      </n-flex>
    </template>
  </n-modal>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useAuthStore } from '@/stores/authStore';
import apiService from '@/services/apiService';
import {
  NLayoutContent, NH2, NList, NListItem, NThing, NButton, NSpace,
  NPopconfirm, NSpin, NEmpty, NModal, NInput, NFlex, NText, useMessage, NRate
} from 'naive-ui';

const authStore = useAuthStore();
const message = useMessage();
const myReviews = ref([]);
const loading = ref(true);

const showEditModal = ref(false);
const isUpdating = ref(false);
const editingReview = ref(null);
const editingCommentText = ref('');
const editingScore = ref(0); // 使用5分制

const fetchMyReviews = async () => {
  loading.value = true;
  try {
    const response = await apiService.getReviewsByUserId(authStore.userId);
    myReviews.value = response.data;
  } catch (error) {
    message.error('加载我的评论失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const openEditModal = (review) => {
  editingReview.value = review;
  editingCommentText.value = review.commentText;
  editingScore.value = review.score / 2; // 后端10分制转为前端5分制
  showEditModal.value = true;
};

const handleUpdate = async () => {
  if (!editingCommentText.value.trim()) {
    message.warning('评论内容不能为空');
    return;
  }
  isUpdating.value = true;
  try {
    // 构造正确的请求体，并将5分制转回10分制
    const payload = {
      commentText: editingCommentText.value,
      score: editingScore.value * 2
    };
    await apiService.updateReview(editingReview.value.id, payload);
    message.success('评论更新成功');
    showEditModal.value = false;
    fetchMyReviews();
  } catch (error) {
    message.error('更新失败: ' + (error.response?.data?.message || error.message));
  } finally {
    isUpdating.value = false;
  }
};

const handleDelete = async (reviewId) => {
  try {
    await apiService.deleteReview(reviewId);
    message.success('删除成功');
    // 过滤掉已删除的评论，实现即时刷新
    myReviews.value = myReviews.value.filter(review => review.id !== reviewId);
  } catch (error) {
    message.error('删除失败');
  }
};

onMounted(() => {
  if (authStore.isAuthenticated) {
    fetchMyReviews();
  }
});
</script>