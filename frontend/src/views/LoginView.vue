<template>
  <div class="login-container">
    <n-card title="登录" :bordered="false" style="width: 100%; max-width: 450px;">
      <n-form ref="formRef" :model="model" :rules="rules" @submit.prevent="handleSubmit">
        <n-form-item path="username" label="用户名">
          <n-input v-model:value="model.username" placeholder="输入你的用户名" />
        </n-form-item>
        <n-form-item path="password" label="密码">
          <n-input
            v-model:value="model.password"
            type="password"
            show-password-on="mousedown"
            placeholder="输入你的密码"
          />
        </n-form-item>
        <n-alert v-if="error" title="登录失败" type="error" class="mb-4">
          {{ error }}
        </n-alert>
        <n-button block type="primary" attr-type="submit">
          登录
        </n-button>
      </n-form>
    </n-card>
  </div>
</template>

<script setup>
// script 部分无需改动
import { ref } from 'vue';
import { useAuthStore } from '@/store/auth';
import { useMessage, NCard, NForm, NFormItem, NInput, NButton, NAlert } from 'naive-ui';

const authStore = useAuthStore();
const message = useMessage();
const formRef = ref(null);
const model = ref({
  username: '',
  password: ''
});
const error = ref(null);

const rules = {
  username: { required: true, message: '请输入用户名', trigger: 'blur' },
  password: { required: true, message: '请输入密码', trigger: 'blur' },
};

const handleSubmit = (e) => {
  e.preventDefault();
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      error.value = null;
      try {
        await authStore.login(model.value);
        message.success('登录成功！');
      } catch (err) {
        error.value = '用户名或密码错误。';
      }
    }
  });
};
</script>

<style scoped>
/* 2. 定义这个容器的样式 */
.login-container {
  flex-grow: 1; /* 让这个容器也撑满父级（.app-content）给它的空间 */
  display: flex;
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
  padding: 24px;
}
</style>