<template>
    <div class="auth-container">
      <n-card class="auth-card">
        <n-tabs
          :default-value="defaultTab"
          size="large"
          animated
          pane-wrapper-style="margin: 0 -4px"
          pane-style="padding-left: 4px; padding-right: 4px; box-sizing: border-box;"
        >
          <n-tab-pane name="signin" tab="登录">
            <n-form ref="loginFormRef" :model="loginFormValue" :rules="loginRules">
              <n-form-item-row label="用户名" path="username">
                <n-input v-model:value="loginFormValue.username" placeholder="请输入用户名" />
              </n-form-item-row>
              <n-form-item-row label="密码" path="password">
                <n-input
                  v-model:value="loginFormValue.password"
                  type="password"
                  show-password-on="mousedown"
                  placeholder="请输入密码"
                  @keyup.enter="handleLogin"
                />
              </n-form-item-row>
            </n-form>
            <n-button type="primary" block strong :loading="loginLoading" @click="handleLogin">
              登录
            </n-button>
             <n-flex justify="center" style="margin-top: 16px;">
                <router-link to="/"><n-button text>返回首页</n-button></router-link>
              </n-flex>
          </n-tab-pane>
          <n-tab-pane name="signup" tab="注册">
            <n-form ref="registerFormRef" :model="registerFormValue" :rules="registerRules">
              <n-form-item-row label="用户名" path="username">
                <n-input v-model:value="registerFormValue.username" placeholder="请输入用户名" />
              </n-form-item-row>
               <n-form-item-row label="邮箱" path="email">
                  <n-input v-model:value="registerFormValue.email" placeholder="请输入邮箱" />
              </n-form-item-row>
              <n-form-item-row label="密码" path="password">
                <n-input
                  v-model:value="registerFormValue.password"
                  type="password"
                  show-password-on="mousedown"
                  placeholder="请输入至少6位的密码"
                />
              </n-form-item-row>
              <n-form-item-row label="重复密码" path="reenteredPassword">
                <n-input
                  v-model:value="registerFormValue.reenteredPassword"
                  type="password"
                  show-password-on="mousedown"
                  placeholder="请再次输入密码"
                  @keyup.enter="handleRegister"
                />
              </n-form-item-row>
            </n-form>
            <n-button type="primary" block strong :loading="registerLoading" @click="handleRegister">
              注册
            </n-button>
             <n-flex justify="center" style="margin-top: 16px;">
                <router-link to="/"><n-button text>返回首页</n-button></router-link>
              </n-flex>
          </n-tab-pane>
        </n-tabs>
      </n-card>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import {
    // 移除了 NLayout
    NCard, NTabs, NTabPane, NForm, NFormItemRow,
    NInput, NButton, NFlex, useMessage
  } from 'naive-ui';
  import { useAuthStore } from '@/stores/authStore';
  import apiService from '@/services/apiService';
  
//   const props = defineProps({
//       defaultTab: {
//           type: String,
//           default: 'signin'
//       }
//   });
  
  // --- script 部分的登录和注册逻辑保持不变 ---
  const router = useRouter();
  const message = useMessage();
  const authStore = useAuthStore();
  const loginFormRef = ref(null);
  const loginLoading = ref(false);
  const loginFormValue = ref({
    username: '',
    password: '',
  });
  const loginRules = {
    username: { required: true, message: '请输入用户名', trigger: 'blur' },
    password: { required: true, message: '请输入密码', trigger: 'blur' },
  };
  const handleLogin = (e) => {
    e.preventDefault();
    loginFormRef.value?.validate(async (errors) => {
      if (!errors) {
        loginLoading.value = true;
        try {
          const response = await apiService.login(loginFormValue.value);
          authStore.setAuth(response.data);
          message.success('登录成功！');
          router.push('/');
        } catch (error) {
          message.error(error.response?.data?.message || '登录失败，请检查用户名或密码');
        } finally {
          loginLoading.value = false;
        }
      }
    });
  };
  const registerFormRef = ref(null);
  const registerLoading = ref(false);
  const registerFormValue = ref({
    username: '',
    email: '',
    password: '',
    reenteredPassword: '',
  });
  const validatePasswordSame = (rule, value) => {
      if (value !== registerFormValue.value.password) {
          return new Error('两次输入的密码不一致');
      }
      return true;
  };
  const registerRules = {
    username: { required: true, message: '用户名不能为空', trigger: 'blur' },
    email: { required: true, type: 'email', message: '请输入正确的邮箱格式', trigger: ['input', 'blur'] },
    password: { required: true, min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
    reenteredPassword: [
      { required: true, message: '请再次输入密码', trigger: ['input', 'blur'] },
      { validator: validatePasswordSame, trigger: 'blur' }
    ]
  };
  const handleRegister = (e) => {
    e.preventDefault();
    registerFormRef.value?.validate(async (errors) => {
      if (!errors) {
        registerLoading.value = true;
        try {
          const payload = {
              username: registerFormValue.value.username,
              email: registerFormValue.value.email,
              password: registerFormValue.value.password,
          };
          await apiService.register(payload);
          message.success('注册成功，请登录！');
          router.push('/login');
        } catch (error) {
          message.error(error.response?.data?.message || '注册失败');
        } finally {
          registerLoading.value = false;
        }
      }
    });
  };
  
  </script>
  
  <style scoped>
  /* 将 .auth-layout 改为 .auth-container */
  .auth-container {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-grow: 1; /* 让这个容器填满 App.vue 布局中的剩余空间 */
    padding: 40px 0; /* 添加一些垂直内边距 */
    /* background-color: #f0f2f5; <-- 移除这一行，让背景色透明，显示出全局背景色 */
  }
  .auth-card {
    width: 400px;
  }
  a {
    text-decoration: none;
  }
  </style>