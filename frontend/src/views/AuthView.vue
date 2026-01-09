<template>
  <div class="auth-container">
    <n-card class="auth-card">
      <n-tabs :default-value="defaultTab" size="large" animated pane-wrapper-style="margin: 0 -4px"
        pane-style="padding-left: 4px; padding-right: 4px; box-sizing: border-box;">
        <n-tab-pane name="signin" tab="登录">
          <n-form ref="loginFormRef" :model="loginFormValue" :rules="loginRules">
            <n-form-item-row label="用户名" path="username">
              <n-input v-model:value="loginFormValue.username" placeholder="请输入用户名" />
            </n-form-item-row>
            <n-form-item-row label="密码" path="password">
              <n-input v-model:value="loginFormValue.password" type="password" show-password-on="mousedown"
                placeholder="请输入密码" @keyup.enter="handleLogin" />
            </n-form-item-row>
            <n-form-item-row label="验证码" path="captcha">
              <n-input-group>
                <n-input v-model:value="loginFormValue.captcha" placeholder="请输入验证码"
                  style="width: 60%;" @keyup.enter="handleLogin" />
                <img v-if="captchaImage" :src="captchaImage" alt="验证码"
                  @click="refreshCaptcha"
                  style="width: 38%; height: 34px; cursor: pointer; border: 1px solid #d9d9d9; border-radius: 3px; margin-left: 2%;"
                  title="点击刷新验证码" />
              </n-input-group>
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
              <n-input v-model:value="registerFormValue.password" type="password" show-password-on="mousedown"
                placeholder="请输入至少6位的密码" />
            </n-form-item-row>
            <n-form-item-row label="重复密码" path="reenteredPassword">
              <n-input v-model:value="registerFormValue.reenteredPassword" type="password" show-password-on="mousedown"
                placeholder="请再次输入密码" @keyup.enter="handleRegister" />
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
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import {
  NCard, NTabs, NTabPane, NForm, NFormItemRow,
  NInput, NButton, NFlex, NInputGroup, useMessage
} from 'naive-ui';
import { useAuthStore } from '@/stores/authStore';
import apiService from '@/services/apiService';

defineProps({
  defaultTab: {
    type: String,
    default: 'signin'
  }
});

const router = useRouter();
const message = useMessage();
const authStore = useAuthStore();

// 验证码相关
const captchaImage = ref('');
const captchaId = ref('');

// 获取验证码
const refreshCaptcha = async () => {
  try {
    const response = await apiService.getCaptcha();
    captchaImage.value = response.data.image;
    captchaId.value = response.data.captchaId;
  } catch (error) {
    console.error('获取验证码失败', error);
    message.error('获取验证码失败，请刷新页面重试');
  }
};

// 组件挂载时获取验证码
onMounted(() => {
  refreshCaptcha();
});

// 登录表单
const loginFormRef = ref(null);
const loginLoading = ref(false);
const loginFormValue = ref({
  username: '',
  password: '',
  captcha: '',
});
const loginRules = {
  username: { required: true, message: '请输入用户名', trigger: 'blur' },
  password: { required: true, message: '请输入密码', trigger: 'blur' },
  captcha: { required: true, message: '请输入验证码', trigger: 'blur' },
};

const handleLogin = async (e) => {
  e.preventDefault();
  try {
    await loginFormRef.value?.validate();
    loginLoading.value = true;

    // 添加验证码ID和验证码
    const loginData = {
      username: loginFormValue.value.username,
      password: loginFormValue.value.password,
      captchaId: captchaId.value,
      captcha: loginFormValue.value.captcha,
    };

    const response = await apiService.login(loginData);
    authStore.setAuth(response.data);
    message.success('登录成功！');
    router.push('/');
  } catch (errors) {
    console.log('登录失败', errors);

    // 验证码错误后刷新验证码
    refreshCaptcha();
    loginFormValue.value.captcha = '';

    if (errors && Array.isArray(errors)) {
      // 表单验证失败
      message.error('请填写完整的登录信息');
    } else if (errors.response) {
      // 后端返回的错误
      const status = errors.response.status;
      const errorMessage = errors.response.data.message;

      if (status === 400 && errorMessage === '验证码错误或已过期') {
        message.error('验证码错误，请重新输入');
      } else if (status === 401 || status === 403) {
        message.error('用户名或密码错误');
      } else {
        message.error(errorMessage || '登录失败，请稍后重试');
      }
    } else {
      message.error('网络错误，请检查网络连接');
    }
  } finally {
    loginLoading.value = false;
  }
};

// 注册表单
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

const handleRegister = async (e) => {
  e.preventDefault();
  try {
    await registerFormRef.value?.validate();
    registerLoading.value = true;
    const payload = {
      username: registerFormValue.value.username,
      email: registerFormValue.value.email,
      password: registerFormValue.value.password,
    };
    await apiService.register(payload);
    message.success('注册成功，请登录！');
    router.push('/login');
  } catch (errors) {
    console.log('注册表单验证失败或API出错', errors);
    if (errors && Array.isArray(errors)) {
      message.error('请检查所有字段是否填写正确。');
    } else if (errors.response) {
      message.error(errors.response.data.message || '注册失败');
    } else {
      message.error('发生未知错误');
    }
  } finally {
    registerLoading.value = false;
  }
};
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-grow: 1;
  padding: 40px 0;
}

.auth-card {
  width: 400px;
}

a {
  text-decoration: none;
}
</style>
