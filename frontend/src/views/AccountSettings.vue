<template>
  <n-layout-content content-style="padding: 24px;">
    <n-grid :x-gap="24" :cols="5">
      <n-gi :span="1">
        <n-menu :options="menuOptions" v-model:value="activeKey" />
      </n-gi>
      <n-gi :span="4" style="min-height: 600px">
        <n-card v-if="activeKey === 'basic'" title="基本资料">
          <n-form ref="formRef" :model="profileForm" label-placement="left" label-width="80">
            <n-form-item label="头像">
              <n-space vertical>
                <n-space align="baseline">
                  <n-avatar :size="96" :src="profileForm.profileImageUrl" />
                  <n-avatar :size="64" :src="profileForm.profileImageUrl" />
                  <n-avatar :size="48" :src="profileForm.profileImageUrl" />
                </n-space>
                <n-upload :custom-request="customAvatarUploadRequest" :show-file-list="false" :max="1" accept="image/*">
                  <n-button :loading="uploadingToHost">更换头像</n-button>
                </n-upload>
              </n-space>
            </n-form-item>
            <n-form-item label="昵称" path="username">
              <n-input v-model:value="profileForm.username" placeholder="输入你的昵称" />
            </n-form-item>
            <n-form-item label="生日">
              <n-input-group>
                <n-date-picker v-model:formatted-value="profileForm.birthDate" value-format="yyyy-MM-dd" type="date"
                  style="width: 250px" />
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
              <n-text>{{ profileForm.email || "未绑定" }}</n-text>
              <n-button text type="primary" style="margin-left: 12px" @click="openBindingModal('email')">
                {{ profileForm.email ? "换绑" : "绑定" }}
              </n-button>
            </n-form-item>
            <n-form-item label="手机号">
              <n-text>{{ maskedPhone || "未绑定" }}</n-text>
              <n-button text type="primary" style="margin-left: 12px" @click="openBindingModal('phone')">
                {{ profileForm.phone ? "换绑" : "绑定" }}
              </n-button>
            </n-form-item>
            <n-form-item label="密码">
              <n-button @click="showPasswordModal = true">修改密码</n-button>
            </n-form-item>
          </n-form>
        </n-card>

        <n-flex justify="end" style="margin-top: 24px">
          <n-button type="primary" @click="handleSave" :loading="saving">保存修改</n-button>
        </n-flex>
      </n-gi>
    </n-grid>
  </n-layout-content>

  <n-modal v-model:show="showPasswordModal" preset="card" title="修改密码" style="width: 450px">
    <n-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-placement="left" label-width="80">
      <n-form-item label="旧密码" path="oldPassword">
        <n-input type="password" show-password-on="mousedown" v-model:value="passwordForm.oldPassword"
          placeholder="请输入当前密码" />
      </n-form-item>
      <n-form-item label="新密码" path="newPassword">
        <n-input type="password" show-password-on="mousedown" v-model:value="passwordForm.newPassword"
          placeholder="请输入新密码" />
      </n-form-item>
      <n-form-item label="确认密码" path="confirmPassword">
        <n-input type="password" show-password-on="mousedown" v-model:value="passwordForm.confirmPassword"
          placeholder="请再次输入新密码" />
      </n-form-item>
    </n-form>
    <template #footer>
      <n-flex justify="end">
        <n-button @click="showPasswordModal = false">取消</n-button>
        <n-button type="primary" @click="handleChangePassword">确认修改</n-button>
      </n-flex>
    </template>
  </n-modal>

  <n-modal v-model:show="showBindingModal" preset="card" :title="bindingTitle" style="width: 480px"
    @after-leave="countdown = 0">
    <n-form ref="bindingFormRef" :model="bindingForm" :rules="bindingRules">
      <n-form-item :label="bindingLabel" path="value">
        <n-input v-model:value="bindingForm.value" :placeholder="bindingPlaceholder" />
      </n-form-item>
      <n-form-item label="验证码" path="code">
        <n-input-group>
          <n-input v-model:value="bindingForm.code" placeholder="请输入任意6位验证码（模拟）" />
          <n-button @click="sendCode" :loading="codeSending" :disabled="isCountingDown">
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
import { ref, onMounted, computed } from "vue";
import { useAuthStore } from "@/stores/authStore";
import apiService from "@/services/apiService";
import {
  NLayoutContent, NGrid, NGi, NMenu, NCard, NForm, NFormItem, NInput,
  NInputGroup, NAvatar, NUpload, NButton, NSpace, NFlex, NDatePicker,
  NText, NModal, useMessage,
} from "naive-ui";
import axios from "axios";

const authStore = useAuthStore();
const message = useMessage();
const activeKey = ref("basic");
const saving = ref(false);
const uploadingToHost = ref(false);

const profileForm = ref({
  username: "", profileImageUrl: "", personalWebsite: "",
  bio: "", birthDate: null, email: "", phone: "",
});

const maskedPhone = computed(() => {
  const phone = profileForm.value.phone;
  if (phone && /^\d{11}$/.test(phone)) {
    return phone.replace(/(\d{3})\d{4}(\d{4})/, "$1****$2");
  }
  return phone;
});

// --- 绑定/更换 Modal 的逻辑 ---
const showBindingModal = ref(false);
const bindingType = ref("");
const bindingFormRef = ref(null);
const bindingForm = ref({ value: "", code: "" });
const codeSending = ref(false);
const countdown = ref(0);
const isCountingDown = computed(() => countdown.value > 0);
const countdownText = computed(() => isCountingDown.value ? `${countdown.value}s 后重试` : "发送验证码");

const bindingRules = computed(() => {
  const rules = {
    code: { required: true, message: "请输入验证码", trigger: ["input", "blur"], },
  };
  if (bindingType.value === "phone") {
    rules.value = {
      required: true, trigger: ["input", "blur"],
      validator(rule, value) {
        if (!value) { return new Error("请输入手机号码"); }
        if (!/^\d{11}$/.test(value)) { return new Error("请输入11位有效的手机号码"); }
        return true;
      },
    };
  } else {
    rules.value = { required: true, type: "email", message: "请输入有效的邮箱地址", trigger: ["input", "blur"], };
  }
  return rules;
});
const bindingTitle = computed(() => {
  const action = profileForm.value[bindingType.value] ? "更换" : "绑定";
  const type = bindingType.value === "email" ? "邮箱" : "手机号";
  return `${action}${type}`;
});
const bindingLabel = computed(() => "新" + (bindingType.value === "email" ? "邮箱地址" : "手机号码"));
const bindingPlaceholder = computed(() => "请输入" + bindingLabel.value);

function openBindingModal(type) {
  bindingType.value = type;
  bindingForm.value = { value: "", code: "" };
  bindingFormRef.value?.restoreValidation();
  countdown.value = 0;
  showBindingModal.value = true;
}

async function sendCode() {
  bindingFormRef.value?.validate(
    (errors) => {
      if (errors) { message.error(errors[0][0].message); return; }
      executeSendCode();
    },
    (rule) => rule.key === "value"
  ).catch(() => { });
}

async function executeSendCode() {
  codeSending.value = true;
  setTimeout(() => {
    message.success("验证码已发送（模拟）");
    codeSending.value = false;
    countdown.value = 60;
    const interval = setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0) clearInterval(interval);
    }, 1000);
  }, 500);
}

// **核心修改 1**: 使用 try...catch 结构重构 submitBinding 函数
async function submitBinding() {
  try {
    await bindingFormRef.value?.validate();
    // 验证通过后，执行模拟成功的逻辑
    message.success(`${bindingTitle.value}成功！`);
    profileForm.value[bindingType.value] = bindingForm.value.value;
    authStore.user[bindingType.value] = bindingForm.value.value;
    localStorage.setItem("user", JSON.stringify(authStore.user));
    showBindingModal.value = false;
  } catch (validationErrors) {
    // 验证失败后，捕获错误并给出提示
    message.error("请按要求填写所有字段。");
    console.log("Form validation failed:", validationErrors);
  }
}

// --- 修改密码相关的完整逻辑 ---
const showPasswordModal = ref(false);
const passwordFormRef = ref(null);
const passwordForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' });
const validatePasswordSame = (rule, value) => {
  if (value !== passwordForm.value.newPassword) {
    return new Error('两次输入的密码不一致');
  }
  return true;
};
const passwordRules = {
  oldPassword: { required: true, message: '请输入旧密码', trigger: 'blur' },
  newPassword: { required: true, min: 6, message: '新密码长度不能少于6位', trigger: 'blur' },
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePasswordSame, trigger: ['input', 'blur'] }
  ]
};

// **核心修改 2**: 使用 try...catch 结构重构 handleChangePassword 函数
const handleChangePassword = async () => {
  try {
    await passwordFormRef.value?.validate();
    // 验证通过后，执行模拟成功的逻辑
    message.success("密码修改成功（模拟）");
    showPasswordModal.value = false;
  } catch (validationErrors) {
    // 验证失败后，捕获错误并给出提示
    message.error("请按要求填写所有字段。");
    console.log("Form validation failed:", validationErrors);
  }
};


// --- 其他逻辑保持不变 ---
onMounted(() => {
  if (authStore.user) {
    profileForm.value = { ...authStore.user };
  }
});

const menuOptions = [{ label: "基本资料", key: "basic" }, { label: "账号安全", key: "security" }];

const customAvatarUploadRequest = async ({ file, onFinish, onError }) => {
  uploadingToHost.value = true;
  const formData = new FormData();
  formData.append("image", file.file);
  formData.append("key", "4312ec520960fe609d17eb3f8a99ca5e");
  try {
    const response = await axios.post("https://api.imgbb.com/1/upload", formData);
    profileForm.value.profileImageUrl = response.data.data.url;
    message.success("头像预览更新成功，请点击下方“保存修改”以生效。");
    onFinish();
  } catch (error) {
    message.error(error.message);
    onError();
  } finally {
    uploadingToHost.value = false;
  }
};

const handleSave = async () => {
  saving.value = true;
  try {
    const payload = {
      username: profileForm.value.username,
      personalWebsite: profileForm.value.personalWebsite,
      bio: profileForm.value.bio,
      birthDate: profileForm.value.birthDate,
      profileImageUrl: profileForm.value.profileImageUrl,
    };
    await apiService.updateUserProfile(payload);
    Object.assign(authStore.user, payload);
    localStorage.setItem("user", JSON.stringify(authStore.user));
    message.success("个人资料更新成功！");
  } catch (error) {
    message.error("保存失败: " + (error.response?.data?.message || error.message));
  } finally {
    saving.value = false;
  }
};
</script>