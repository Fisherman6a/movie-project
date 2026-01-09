<template>
  <n-layout-content content-style="padding: 24px; max-width: 900px; margin: auto;">
    <n-card>
      <n-space align="center">
        <n-avatar :size="120" :src="user.profileImageUrl" />
        <n-space vertical :size="12">
          <n-h1 style="margin: 0;">{{ user.username }}</n-h1>

          <div v-if="!isEditingBio" @click="startEditingBio" style="cursor: pointer; min-height: 24px;" title="点击编辑签名">
            <n-p depth="3">{{ user.bio || '这位用户很神秘，什么都没留下...' }}</n-p>
          </div>
          <div v-else>
            <n-input-group>
              <n-input ref="bioInputRef" type="textarea" v-model:value="editingBioText" placeholder="输入您的新签名..."
                :autosize="{ minRows: 1, maxRows: 3 }" />
              <n-button type="primary" @click="saveBio" :loading="isSavingBio">保存</n-button>
              <n-button @click="cancelEditingBio">取消</n-button>
            </n-input-group>
          </div>
          <n-space>
            <n-button tag="a" :href="user.personalWebsite" target="_blank" v-if="user.personalWebsite" text
              type="primary">
              <template #icon>
                <n-icon :component="LinkIcon" />
              </template>
              访问个人网站
            </n-button>
            <n-p depth="3" v-if="user.createdAt" style="margin: 0;">
              <n-icon :component="TimeIcon" :size="14" style="vertical-align: -0.15em; margin-right: 4px;" />
              于 {{ formattedJoinDate }} 加入
            </n-p>
          </n-space>
        </n-space>
      </n-space>
    </n-card>

    <n-h2 prefix="bar">
      <n-text type="primary">我的影评</n-text>
    </n-h2>
    <n-spin :show="loadingReviews">
      <n-list bordered>
        <n-list-item v-for="review in myReviews" :key="review.id">
          <n-thing>
            <template #header>
              对电影
              <router-link :to="{ name: 'MovieDetail', params: { id: review.movieId } }">
                《{{ review.movieTitle }}》
              </router-link>
              的评论
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
          <n-empty description="还没有发表过任何评论。" />
        </n-list-item>
      </n-list>
    </n-spin>

  </n-layout-content>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue';
import { useAuthStore } from '@/stores/authStore';
import apiService from '@/services/apiService';
import { NLayoutContent, NCard, NSpace, NAvatar, NH1, NH2, NP, NText, NButton, NIcon, NList, NListItem, NThing, NSpin, NEmpty, NInput, NInputGroup, useMessage, NRate } from 'naive-ui';
import { Link as LinkIcon, TimeOutline as TimeIcon } from '@vicons/ionicons5';

const authStore = useAuthStore();
const message = useMessage();
const user = ref({});
const myReviews = ref([]);
const loadingReviews = ref(true);

const isEditingBio = ref(false);
const isSavingBio = ref(false);
const editingBioText = ref('');
const bioInputRef = ref(null);

const startEditingBio = async () => {
  isEditingBio.value = true;
  editingBioText.value = user.value.bio || '';
  await nextTick();
  bioInputRef.value?.focus();
};

const cancelEditingBio = () => {
  isEditingBio.value = false;
};

const saveBio = async () => {
  isSavingBio.value = true;
  try {
    const payload = {
      username: user.value.username,
      personalWebsite: user.value.personalWebsite,
      birthDate: user.value.birthDate,
      bio: editingBioText.value,
    };
    await apiService.updateUserProfile(payload);

    user.value.bio = editingBioText.value;
    authStore.user.bio = editingBioText.value;
    localStorage.setItem('user', JSON.stringify(authStore.user));

    message.success('签名更新成功！');
    isEditingBio.value = false;
  } catch (error) {
    message.error('保存失败，请重试');
    console.error(error);
  } finally {
    isSavingBio.value = false;
  }
};

const formattedJoinDate = computed(() => {
  if (!user.value.createdAt) {
    return '';
  }
  const date = new Date(user.value.createdAt);
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
});

onMounted(async () => {
  if (authStore.user) {
    user.value = { ...authStore.user };
  }

  loadingReviews.value = true;
  try {
    const response = await apiService.getReviewsByUserId(authStore.userId);
    myReviews.value = response.data;
  } catch (error) {
    console.error('加载评论失败:', error);
  } finally {
    loadingReviews.value = false;
  }
});
</script>

<style scoped>
a {
  text-decoration: none;
  color: #36ad6a;
  font-weight: bold;
}

a:hover {
  text-decoration: underline;
}
</style>