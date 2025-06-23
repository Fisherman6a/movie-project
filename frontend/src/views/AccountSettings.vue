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

  <!-- 修改密码 Modal -->
  <n-modal v-model:show="showPasswordModal" preset="card" title="修改密码" style="width: 450px">
    <!-- ... (修改密码的表单内容保持不变) ... -->
  </n-modal>

  <!-- 绑定/更换 邮箱/手机号 Modal -->
  <n-modal v-model:show="showBindingModal" preset="card" :title="bindingTitle" style="width: 480px"
    @after-leave="countdown = 0">
    <n-form ref="bindingFormRef" :model="bindingForm" :rules="bindingRules">
      <n-form-item :label="bindingLabel" path="value">
        <n-input v-model:value="bindingForm.value" :placeholder="bindingPlaceholder" />
      </n-form-item>
      <n-form-item label="验证码" path="code">
        <n-input-group>
          <n-input v-model:value="bindingForm.code" placeholder="请输入6位验证码" />
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
  NLayoutContent,
  NGrid,
  NGi,
  NMenu,
  NCard,
  NForm,
  NFormItem,
  NInput,
  NInputGroup,
  NAvatar,
  NUpload,
  NButton,
  NSpace,
  NFlex,
  NDatePicker,
  NText,
  NModal,
  useMessage,
} from "naive-ui";
import axios from "axios";

const authStore = useAuthStore();
const message = useMessage();
const activeKey = ref("basic");
const saving = ref(false);
const showPasswordModal = ref(false);
const uploadingToHost = ref(false);

const profileForm = ref({
  username: "",
  profileImageUrl: "",
  personalWebsite: "",
  bio: "",
  birthDate: null,
  email: "",
  phone: "",
});

const maskedPhone = computed(() => {
  const phone = profileForm.value.phone;
  if (phone && /^\d{11}$/.test(phone)) {
    return phone.replace(/(\d{3})\d{4}(\d{4})/, "$1****$2");
  }
  return phone;
});

// --- 绑定/更换 Modal 的完整逻辑 ---
const showBindingModal = ref(false);
const bindingType = ref(""); // 'email' or 'phone'
const bindingFormRef = ref(null);
const bindingForm = ref({ value: "", code: "" });

const bindingRules = computed(() => {
  const rules = {
    code: {
      required: true,
      message: "请输入验证码",
      trigger: ["input", "blur"],
    },
  };
  if (bindingType.value === "phone") {
    rules.value = {
      required: true,
      trigger: ["input", "blur"],
      validator(rule, value) {
        if (!value) {
          return new Error("请输入手机号码");
        }
        if (!/^\d{11}$/.test(value)) {
          return new Error("请输入11位有效的手机号码");
        }
        return true;
      },
    };
  } else {
    // email rules
    rules.value = {
      required: true,
      type: "email",
      message: "请输入有效的邮箱地址",
      trigger: ["input", "blur"],
    };
  }
  return rules;
});

const codeSending = ref(false);
const countdown = ref(0);
const isCountingDown = computed(() => countdown.value > 0);
const countdownText = computed(() =>
  isCountingDown.value ? `${countdown.value}s 后重试` : "发送验证码"
);

const bindingTitle = computed(() => {
  const action = profileForm.value[bindingType.value] ? "更换" : "绑定";
  const type = bindingType.value === "email" ? "邮箱" : "手机号";
  return `${action}${type}`;
});

const bindingLabel = computed(
  () => "新" + (bindingType.value === "email" ? "邮箱地址" : "手机号码")
);
const bindingPlaceholder = computed(() => "请输入" + bindingLabel.value);

function openBindingModal(type) {
  bindingType.value = type;
  bindingForm.value = { value: "", code: "" };
  bindingFormRef.value?.restoreValidation();
  countdown.value = 0;
  showBindingModal.value = true;
}

async function sendCode() {
  // 仅验证目标字段
  bindingFormRef.value
    ?.validate(
      (errors) => {
        if (errors) {
          message.error(errors[0][0].message);
          return;
        }
        // 验证通过，执行发送逻辑
        executeSendCode();
      },
      (rule) => rule.key === "value"
    )
    .catch(() => { }); // 捕获并忽略Promise拒绝，因为我们已经在回调中处理了
}

async function executeSendCode() {
  codeSending.value = true;
  try {
    // 调用真实API
    await apiService.sendVerificationCode({
      destination: bindingForm.value.value,
      type: bindingType.value.toUpperCase(), // 'EMAIL' or 'PHONE'
    });

    message.success("验证码已发送，请注意查收");
    countdown.value = 60;
    const interval = setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0) {
        clearInterval(interval);
      }
    }, 1000);
  } catch (error) {
    message.error(
      "发送失败: " + (error.response?.data?.message || error.message)
    );
  } finally {
    codeSending.value = false;
  }
}

async function submitBinding() {
  try {
    await bindingFormRef.value?.validate();

    const apiCall =
      bindingType.value === "email"
        ? apiService.changeEmail({
          newEmail: bindingForm.value.value,
          verificationCode: bindingForm.value.code,
        })
        : apiService.changePhone({
          newPhone: bindingForm.value.value,
          verificationCode: bindingForm.value.code,
        });

    await apiCall;

    message.success(`${bindingTitle.value}成功！`);
    profileForm.value[bindingType.value] = bindingForm.value.value;
    authStore.user[bindingType.value] = bindingForm.value.value;
    localStorage.setItem("user", JSON.stringify(authStore.user));
    showBindingModal.value = false;
  } catch (error) {
    if (Array.isArray(error)) {
      // 这是表单验证错误
      message.error("请检查输入内容是否正确");
    } else {
      // 这是API错误
      message.error(
        "操作失败: " + (error.response?.data?.message || error.message)
      );
    }
  }
}

// --- 其他逻辑 ---
onMounted(() => {
  if (authStore.user) {
    profileForm.value = { ...authStore.user };
  }
});

const menuOptions = [
  { label: "基本资料", key: "basic" },
  { label: "账号安全", key: "security" },
];

const uploadToImageHost = async (file) => {
  const imgbbApiKey = "4312ec520960fe609d17eb3f8a99ca5e";
  const formData = new FormData();
  formData.append("image", file);
  formData.append("key", imgbbApiKey);
  try {
    const response = await axios.post(
      "https://api.imgbb.com/1/upload",
      formData
    );
    return response.data.data.url;
  } catch (error) {
    throw new Error(
      "上传到图床失败: " +
      (error.response?.data?.error?.message || error.message)
    );
  }
};

const customAvatarUploadRequest = async ({ file, onFinish, onError }) => {
  uploadingToHost.value = true;
  try {
    const imageUrl = await uploadToImageHost(file.file);
    profileForm.value.profileImageUrl = imageUrl;
    authStore.user.profileImageUrl = imageUrl;
    localStorage.setItem("user", JSON.stringify(authStore.user));
    message.success("头像更新成功");
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
    message.error(
      "保存失败: " + (error.response?.data?.message || error.message)
    );
  } finally {
    saving.value = false;
  }
};
</script>
