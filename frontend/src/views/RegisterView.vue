<template>
  <n-card title="注册新用户" :bordered="false" style="max-width: 450px; margin: 40px auto 0;">
    <n-form ref="formRef" :model="model" :rules="rules" @submit.prevent="handleSubmit">
      <n-form-item path="username" label="用户名">
        <n-input v-model:value="model.username" placeholder="3-50个字符" />
      </n-form-item>
      <n-form-item path="email" label="邮箱">
        <n-input v-model:value="model.email" placeholder="请输入有效的邮箱地址" />
      </n-form-item>
      <n-form-item path="password" label="密码">
        <n-input
          v-model:value="model.password"
          type="password"
          show-password-on="mousedown"
          placeholder="至少6个字符"
        />
      </n-form-item>
      <n-form-item path="reenteredPassword" label="确认密码">
        <n-input
          v-model:value="model.reenteredPassword"
          type="password"
          show-password-on="mousedown"
          placeholder="请再次输入密码"
        />
      </n-form-item>
      <n-button block type="primary" attr-type="submit" :loading="loading">
        立即注册
      </n-button>
    </n-form>
  </n-card>
</template>
  <script setup>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { useMessage, NCard, NForm, NFormItem, NInput, NButton } from 'naive-ui';
  import { useAuthStore } from '@/store/auth';
  
  const authStore = useAuthStore();
  const router = useRouter();
  const message = useMessage();
  const formRef = ref(null);
  const loading = ref(false);
  
  const model = ref({
    username: '',
    email: '',
    password: '',
    reenteredPassword: '',
  });
  
  function validatePasswordSame(rule, value) {
    return value === model.value.password;
  }
  
  const rules = {
    username: { required: true, message: '请输入用户名', trigger: 'blur' },
    email: { required: true, type: 'email', message: '请输入有效的邮箱', trigger: ['input', 'blur'] },
    password: { required: true, message: '请输入密码', trigger: 'blur' },
    reenteredPassword: [
      { required: true, message: '请再次输入密码', trigger: ['input', 'blur'] },
      { validator: validatePasswordSame, message: '两次输入的密码不一致', trigger: 'blur' }
    ]
  };
  
  const handleSubmit = (e) => {
    e.preventDefault();
    formRef.value?.validate(async (errors) => {
      if (!errors) {
        loading.value = true;
        try {
          await authStore.register({
            username: model.value.username,
            password: model.value.password,
            email: model.value.email
          });
          message.success('注册成功！将跳转到登录页面。');
          router.push('/login');
        } catch (err) {
          message.error(err.response?.data?.message || '注册失败，用户名或邮箱可能已被使用。');
        } finally {
          loading.value = false;
        }
      }
    });
  };
  </script>