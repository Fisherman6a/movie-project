<template>
  <n-layout-content content-style="padding: 24px;">
    <n-grid :x-gap="24" :cols="5">
      <n-gi :span="1">
        <n-menu :options="menuOptions" v-model:value="activeKey" />
      </n-gi>
      <n-gi :span="4" style="min-height: 600px">
        <n-card v-if="activeKey === 'basic'" title="基本资料">
          <n-form
            ref="formRef"
            :model="profileForm"
            label-placement="left"
            label-width="80"
          >
            <n-form-item label="头像">
              <n-space vertical>
                <n-space align="baseline">
                  <n-avatar :size="96" :src="profileForm.profileImageUrl" />
                  <n-avatar :size="64" :src="profileForm.profileImageUrl" />
                  <n-avatar :size="48" :src="profileForm.profileImageUrl" />
                </n-space>
                <n-upload
                  :custom-request="customAvatarUploadRequest"
                  :show-file-list="false"
                  :max="1"
                  accept="image/*"
                >
                  <n-button :loading="uploadingToHost">更换头像</n-button>
                </n-upload>
              </n-space>
            </n-form-item>
            <n-form-item label="昵称" path="username">
              <n-input
                v-model:value="profileForm.username"
                placeholder="输入你的昵称"
              />
            </n-form-item>
            <n-form-item label="生日">
              <n-input-group>
                <n-date-picker
                  v-model:formatted-value="profileForm.birthDate"
                  value-format="yyyy-MM-dd"
                  type="date"
                  style="width: 250px"
                />
                <n-button @click="profileForm.birthDate = null">清空</n-button>
              </n-input-group>
            </n-form-item>
            <n-form-item label="个人主页" path="personalWebsite">
              <n-input-group>
                <n-button>http://</n-button>
                <n-input
                  v-model:value="profileForm.personalWebsite"
                  placeholder="你的个人网站"
                />
              </n-input-group>
            </n-form-item>
            <n-form-item label="自我介绍" path="bio">
              <n-input
                v-model:value="profileForm.bio"
                type="textarea"
                placeholder="介绍一下自己吧"
                :autosize="{ minRows: 3, maxRows: 5 }"
              />
            </n-form-item>
          </n-form>
        </n-card>

        <n-card v-if="activeKey === 'security'" title="账号安全">
          <n-form label-placement="left" label-width="80">
            <n-form-item label="邮箱">
              <n-text>{{ profileForm.email || "未绑定" }}</n-text>
              <n-button
                text
                type="primary"
                style="margin-left: 12px"
                @click="openBindingModal('email')"
              >
                {{ profileForm.email ? "换绑" : "绑定" }}
              </n-button>
            </n-form-item>
            <n-form-item label="手机号">
              <n-text>{{ maskedPhone || "未绑定" }}</n-text>
              <n-button
                text
                type="primary"
                style="margin-left: 12px"
                @click="openBindingModal('phone')"
              >
                {{ profileForm.phone ? "换绑" : "绑定" }}
              </n-button>
            </n-form-item>
            <n-form-item label="密码">
              <n-button @click="showPasswordModal = true">修改密码</n-button>
            </n-form-item>
          </n-form>
        </n-card>

        <n-flex justify="end" style="margin-top: 24px">
          <n-button type="primary" @click="handleSave" :loading="saving"
            >保存修改</n-button
          >
        </n-flex>
      </n-gi>
    </n-grid>
  </n-layout-content>

  <!-- 修改密码 Modal -->
  <n-modal
    v-model:show="showPasswordModal"
    preset="card"
    title="修改密码"
    style="width: 450px"
  >
    <n-form>
      <n-form-item label="旧密码">
        <n-input
          v-model:value="passwordForm.oldPassword"
          type="password"
          show-password-on="click"
          placeholder="请输入旧密码"
        />
      </n-form-item>
      <n-form-item label="新密码">
        <n-input
          v-model:value="passwordForm.newPassword"
          type="password"
          show-password-on="click"
          placeholder="请输入新密码"
        />
      </n-form-item>
      <n-form-item label="确认新密码">
        <n-input
          v-model:value="passwordForm.confirmPassword"
          type="password"
          show-password-on="click"
          placeholder="请再次输入新密码"
        />
      </n-form-item>
    </n-form>
    <template #footer>
      <n-flex justify="end">
        <n-button @click="showPasswordModal = false">取消</n-button>
        <n-button type="primary" @click="handleChangePassword"
          >确认修改</n-button
        >
      </n-flex>
    </template>
  </n-modal>

  <!-- ========== NEW CODE: BINDING MODAL START ========== -->
  <n-modal
    v-model:show="showBindingModal"
    preset="card"
    :title="bindingTitle"
    style="width: 480px"
    @after-leave="countdown = 0"
  >
    <n-form>
      <n-form-item :label="bindingLabel">
        <n-input
          v-model:value="bindingForm.value"
          :placeholder="bindingPlaceholder"
        />
      </n-form-item>
      <n-form-item label="验证码">
        <n-input-group>
          <n-input
            v-model:value="bindingForm.code"
            placeholder="请输入6位验证码"
          />
          <n-button
            @click="sendCode"
            :loading="codeSending"
            :disabled="isCountingDown"
          >
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
  <!-- ========== NEW CODE: BINDING MODAL END ========== -->
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
import axios from "axios"; // 引入 axios 用于上传到图床

const authStore = useAuthStore();
const message = useMessage();
const activeKey = ref("basic");
const saving = ref(false);
const showPasswordModal = ref(false);
const uploadingToHost = ref(false); // 新增：图床上传状态

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
  if (phone && phone.length === 11) {
    return phone.replace(/(\d{3})\d{4}(\d{4})/, "$1****$2");
  }
  return phone;
});

// --- Start: Logic for the binding modal ---
const showBindingModal = ref(false);
const bindingType = ref(""); // 'email' or 'phone'
const bindingForm = ref({ value: "", code: "" });

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

const passwordForm = ref({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

function openBindingModal(type) {
  bindingType.value = type;
  bindingForm.value = { value: "", code: "" }; // Reset form
  countdown.value = 0; // Reset countdown
  showBindingModal.value = true;
}

async function sendCode() {
  if (!bindingForm.value.value) {
    message.error(
      `请输入${bindingType.value === "email" ? "邮箱地址" : "手机号码"}`
    );
    return;
  }
  codeSending.value = true;

  try {
    // This is a placeholder for your actual API call
    console.log(`向 ${bindingForm.value.value} 发送验证码...`);
    // Example using your apiService structure:
    // const apiCall = bindingType.value === 'email'
    //   ? apiService.sendEmailVerificationCode({ email: bindingForm.value.value })
    //   : apiService.sendPhoneVerificationCode({ phone: bindingForm.value.value });
    // await apiCall;

    // --- Start: Mock API call for demonstration ---
    await new Promise((resolve) => setTimeout(resolve, 1000));
    // --- End: Mock API call ---

    message.success("验证码已发送，请注意查收");
    countdown.value = 60;
    const interval = setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0) {
        clearInterval(interval);
      }
    }, 1000);
  } catch (error) {
    message.error("发送验证码失败: " + (error.message || "请稍后重试"));
    console.error("发送验证码失败:", error);
  } finally {
    codeSending.value = false;
  }
}

async function submitBinding() {
  if (!bindingForm.value.value || !bindingForm.value.code) {
    message.error("请填写完整信息。");
    return;
  }
  try {
    // This is a placeholder for your actual API call
    console.log(`提交绑定信息:`, bindingForm.value);
    // Example using your apiService structure:
    // const apiCall = bindingType.value === 'email'
    //   ? apiService.changeEmail({ newEmail: bindingForm.value.value, verificationCode: bindingForm.value.code })
    //   : apiService.changePhone({ newPhone: bindingForm.value.value, verificationCode: bindingForm.value.code });
    // await apiCall;

    // --- Start: Mock API call for demonstration ---
    await new Promise((resolve) => setTimeout(resolve, 1000));
    // --- End: Mock API call ---

    message.success(`${bindingTitle.value}成功！`);
    // Update local state
    profileForm.value[bindingType.value] = bindingForm.value.value;
    authStore.user[bindingType.value] = bindingForm.value.value;
    localStorage.setItem("user", JSON.stringify(authStore.user));
    showBindingModal.value = false;
  } catch (error) {
    message.error("操作失败: " + (error.message || "验证码错误或已过期"));
    console.error("绑定失败:", error);
  }
}
// --- End: Logic for the binding modal ---

onMounted(() => {
  if (authStore.user) {
    // For demonstration, I'm keeping your hardcoded phone.
    // In a real app, this would come from authStore.user.phone
    const phone = authStore.user.phone || "18212347833";
    profileForm.value = {
      ...authStore.user,
      phone: phone, // ensure phone is populated for the demo
    };
  }
});

const menuOptions = [
  { label: "基本资料", key: "basic" },
  { label: "账号安全", key: "security" },
];

/**
 * !重要: 此函数需要您根据自己的图床服务进行修改
 * @param {File} file - 用户选择的文件对象
 */
const uploadToImageHost = async (file) => {
  // --- 请在此处替换为您的图床上传逻辑 ---

  // ImgBB API 端点
  const imgbbUploadUrl = "https://api.imgbb.com/1/upload";

  // 重要: 将 'YOUR_IMGBB_API_KEY' 替换为你的实际 ImgBB API 密钥。
  // 你可以在注册并登录 ImgBB 后获取一个密钥：https://imgbb.com/account/api
  const imgbbApiKey = "4312ec520960fe609d17eb3f8a99ca5e";

  const formData = new FormData();
  formData.append("image", file); // ImgBB 要求文件字段名为 'image'
  formData.append("key", imgbbApiKey); // 你的 ImgBB API 密钥

  try {
    const response = await axios.post(imgbbUploadUrl, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    // ImgBB 通常返回的数据格式是 { data: { url: '...' } }
    // 我们通过 response.data.data.url 获取直接的图片 URL
    return response.data.data.url;
  } catch (error) {
    console.error("上传到图床失败:", error);
    // 你可能希望在此处提供一个更友好的错误消息
    throw new Error(
      "上传到图床失败: " +
        (error.response?.data?.error?.message || error.message)
    );
  }
};

const customAvatarUploadRequest = async ({
  file,
  onFinish,
  onError,
}) => {
  uploadingToHost.value = true; // Set loading state
  try {
    // Call your ImgBB upload function
    const imageUrl = await uploadToImageHost(file.file);

    // Update profile image URL
    profileForm.value.profileImageUrl = imageUrl;
    authStore.user.profileImageUrl = imageUrl;
    localStorage.setItem("user", JSON.stringify(authStore.user));

    message.success("头像更新成功");
    onFinish(); // Notify n-upload that the file is finished
  } catch (error) {
    message.error("头像上传失败");
    onError(); // Notify n-upload that an error occurred
  } finally {
    uploadingToHost.value = false; // Reset loading state
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
      // Note: email, phone, and password are changed in their own modals
    };
    await apiService.updateUserProfile(payload);

    // Update the store and localStorage with the new data
    Object.assign(authStore.user, payload);
    localStorage.setItem("user", JSON.stringify(authStore.user));

    message.success("个人资料更新成功！");
  } catch (error) {
    message.error("保存失败，请重试。");
    console.error(error);
  } finally {
    saving.value = false;
  }
};

// Placeholder for the actual change password logic
const handleChangePassword = () => {
  message.info("修改密码逻辑待实现");
  console.log("Password form submitted:", passwordForm.value);
  showPasswordModal.value = false;
};
</script>
