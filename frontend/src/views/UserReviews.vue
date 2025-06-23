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
              <n-text depth="3">发表于: {{ new Date(review.createdAt).toLocaleString() }}</n-text>
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
    <n-input v-model:value="editingCommentText" type="textarea" placeholder="输入新的评论内容..." :autosize="{ minRows: 5 }" />
    <template #footer>
      <n-flex justify="end">
        <n-button @click="showEditModal = false">取消</n-button>
        <n-button type="primary" @click="handleUpdate">保存修改</n-button>
      </n-flex>
    </template>
  </n-modal>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useAuthStore } from '@/stores/authStore';
import apiService from '@/services/apiService';
// import TheHeader from '@/components/TheHeader.vue';
import {
  NLayoutContent, NH2, NList, NListItem, NThing, NButton, NSpace,
  NPopconfirm, NSpin, NEmpty, NModal, NInput, NFlex, NText, useMessage
} from 'naive-ui';

const authStore = useAuthStore();
const message = useMessage();
const myReviews = ref([]);
const loading = ref(true);

// 编辑模态框相关
const showEditModal = ref(false);
const editingReviewId = ref(null);
const editingCommentText = ref('');

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
  editingReviewId.value = review.id;
  editingCommentText.value = review.commentText;
  showEditModal.value = true;
};

const handleUpdate = async () => {
  if (!editingCommentText.value.trim()) {
    message.warning('评论内容不能为空');
    return;
  }
  try {
    await apiService.updateReview(editingReviewId.value, editingCommentText.value);
    message.success('评论更新成功');
    showEditModal.value = false;
    fetchMyReviews(); // 重新加载列表
  } catch (error) {
    message.error('更新失败');
  }
};

const handleDelete = async (reviewId) => {
  try {
    await apiService.deleteReview(reviewId);
    message.success('删除成功');
    fetchMyReviews(); // 重新加载列表
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