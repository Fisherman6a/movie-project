<template>
    <n-layout-content content-style="padding: 24px;">
      <n-grid :x-gap="24" :cols="5">
        <n-gi :span="1">
          <n-menu :options="menuOptions" v-model:value="activeKey" />
        </n-gi>
        <n-gi :span="4" style="min-height: 600px;">
          <n-card v-if="activeKey === 'basic'" title="基本资料">
            <n-form ref="formRef" :model="profileForm" label-placement="left" label-width="80">
              <n-form-item label="头像">
                <n-space vertical>
                  <n-space align="baseline">
                    <n-avatar :size="96" :src="profileForm.profileImageUrl" />
                    <n-avatar :size="64" :src="profileForm.profileImageUrl" />
                    <n-avatar :size="48" :src="profileForm.profileImageUrl" />
                  </n-space>
                  <n-upload action="/api/upload" :show-file-list="true" :max="1" accept="image/*"
                    @finish="handleAvatarUpload">
                    <n-button>选择文件</n-button>
                  </n-upload>
                </n-space>
              </n-form-item>
              <n-form-item label="昵称" path="username">
                <n-input v-model:value="profileForm.username" placeholder="输入你的昵称" />
              </n-form-item>
              <n-form-item label="生日">
                <n-input-group>
                  <n-date-picker v-model:formatted-value="profileForm.birthDate" value-format="yyyy-MM-dd" type="date"
                    style="width: 250px;" />
                  <n-button @click="profileForm.birthDate = null">清空</n-button>
                </n-input-group>
              </n-form-item>
              <n-form-item label="个人主页" path="personalWebsite">
                <n-input-group>
                  <n-button>http://</n-button>
                  <n-input v-model:value="profileForm.personalWebsite" placeholder="你的个人网站" />
                </n-input-group>
              </n-form-item>
              <n-form-item label="自我介绍" path="bio">
                <n-input v-model:value="profileForm.bio" type="textarea" placeholder="介绍一下自己吧"
                  :autosize="{ minRows: 3, maxRows: 5 }" />
              </n-form-item>
            </n-form>
          </n-card>
  
          <n-card v-if="activeKey === 'security'" title="账号安全">
            <n-form label-placement="left" label-width="80">
              <n-form-item label="邮箱">
                <n-text>{{ profileForm.email || '未绑定' }}</n-text>
                <n-button text type="primary" style="margin-left: 12px" @click="openBindingModal('email')">
                  {{ profileForm.email ? '换绑' : '绑定' }}
                </n-button>
              </n-form-item>
              <n-form-item label="手机号">
                <n-text>{{ maskedPhone || '未绑定' }}</n-text>
                <n-button text type="primary" style="margin-left: 12px" @click="openBindingModal('phone')">
                  {{ profileForm.phone ? '换绑' : '绑定' }}
                </n-button>
              </n-form-item>
              <n-form-item label="密码">
                <n-button @click="showPasswordModal = true">修改密码</n-button>
              </n-form-item>
            </n-form>
          </n-card>
  
          <n-flex justify="end" style="margin-top: 24px;">
            <n-button type="primary" @click="handleSave" :loading="saving">保存修改</n-button>
          </n-flex>
        </n-gi>
      </n-grid>
    </n-layout-content>
  
    <n-modal v-model:show="showBindingModal" preset="card" :title="bindingTitle" style="width: 450px;">
      <n-form>
        <n-form-item :label="bindingLabel">
          <n-input v-model:value="bindingForm.value" :placeholder="bindingPlaceholder" />
        </n-form-item>
        <n-form-item label="验证码">
          <n-input-group>
            <n-input v-model:value="bindingForm.code" placeholder="请输入6位验证码" />
            <n-button @click="sendCode" :disabled="isCountingDown" :loading="codeSending">
              {{ countdownText }}
            </n-button>
          </n-input-group>
        </n-form-item>
      </n-form>
      <template #footer>
        <n-flex justify="end">
          <n-button @click="showBindingModal = false">取消</n-button>
          <n-button type="primary" @click="submitBinding">确认</n-button>
        </n-flex>
      </template>
    </n-modal>
  </template>
  
  <script setup>
  import { ref, onMounted, computed } from 'vue';
  import { useAuthStore } from '@/stores/authStore';
  import apiService from '@/services/apiService';
  import {
    NLayoutContent, NGrid, NGi, NMenu, NCard, NForm, NFormItem, NInput, NInputGroup,
    NAvatar, NUpload, NButton, NSpace, NFlex, NDatePicker, NText, NModal, useMessage
  } from 'naive-ui';
  
  const authStore = useAuthStore();
  const message = useMessage();
  const activeKey = ref('basic');
  const saving = ref(false);
  const showPasswordModal = ref(false);
  
  const profileForm = ref({
    username: '',
    profileImageUrl: '',
    personalWebsite: '',
    bio: '',
    birthDate: null,
    email: '',
    phone: ''
  });
  
  const maskedPhone = computed(() => {
    const phone = profileForm.value.phone;
    if (phone && phone.length === 11) {
      return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
    }
    return phone;
  });
  
  const showBindingModal = ref(false);
  const bindingType = ref('');
  const bindingForm = ref({ value: '', code: '' });
  const codeSending = ref(false);
  const countdown = ref(0);
  const isCountingDown = computed(() => countdown.value > 0);
  const countdownText = computed(() => isCountingDown.value ? `${countdown.value}s 后重试` : '发送验证码');
  const bindingTitle = computed(() => (profileForm.value[bindingType.value] ? '更换' : '绑定') + (bindingType.value === 'email' ? '邮箱' : '手机号'));
  const bindingLabel = computed(() => '新' + (bindingType.value === 'email' ? '邮箱地址' : '手机号码'));
  const bindingPlaceholder = computed(() => '请输入' + bindingLabel.value);
  
  function openBindingModal(type) {
    bindingType.value = type;
    bindingForm.value = { value: '', code: '' };
    showBindingModal.value = true;
  }
  
  function sendCode() {
    codeSending.value = true;
    console.log(`向 ${bindingForm.value.value} 发送验证码...`);
    setTimeout(() => {
      codeSending.value = false;
      message.success("验证码已发送（模拟）");
      countdown.value = 60;
      const interval = setInterval(() => {
        countdown.value--;
        if (countdown.value <= 0) {
          clearInterval(interval);
        }
      }, 1000);
    }, 1000);
  }
  
  function submitBinding() {
    console.log(`提交绑定信息:`, { type: bindingType.value, ...bindingForm.value });
    message.success("绑定成功（模拟）");
    profileForm.value[bindingType.value] = bindingForm.value.value;
    showBindingModal.value = false;
  }
  
  onMounted(() => {
    if (authStore.user) {
      profileForm.value = {
        username: authStore.user.username,
        profileImageUrl: authStore.user.profileImageUrl,
        personalWebsite: authStore.user.personalWebsite,
        bio: authStore.user.bio,
        birthDate: authStore.user.birthDate,
        email: authStore.user.email,
        phone: '18212347833'
      };
    }
  });
  
  const menuOptions = [
    { label: '基本资料', key: 'basic' },
    { label: '账号安全', key: 'security' }
  ];
  
  const handleAvatarUpload = ({ file }) => {
    if (file.status === 'finished') {
      const response = JSON.parse(file.response);
      const newAvatarUrl = response.data.url;
      profileForm.value.profileImageUrl = newAvatarUrl;
      authStore.user.profileImageUrl = newAvatarUrl;
      localStorage.setItem('user', JSON.stringify(authStore.user));
      message.success('头像更新成功');
    } else if (file.status === 'error') {
      message.error('头像上传失败');
    }
  };
  
  const handleSave = async () => {
    saving.value = true;
    try {
      const payload = {
        username: profileForm.value.username,
        personalWebsite: profileForm.value.personalWebsite,
        bio: profileForm.value.bio,
        birthDate: profileForm.value.birthDate
      };
      await apiService.updateUserProfile(payload);
  
      authStore.user.username = payload.username;
      authStore.user.personalWebsite = payload.personalWebsite;
      authStore.user.bio = payload.bio;
      authStore.user.birthDate = payload.birthDate;
      localStorage.setItem('user', JSON.stringify(authStore.user));
  
      message.success('个人资料更新成功！');
    } catch (error) {
      message.error('保存失败，请重试。');
      console.error(error);
    } finally {
      saving.value = false;
    }
  };
  </script>