<template>
    <n-form ref="formRef" :model="model" :rules="rules" label-placement="left" label-width="80">
        <n-form-item label="姓名" path="name">
            <n-input v-model:value="model.name" placeholder="输入姓名" />
        </n-form-item>
        <n-form-item label="性别" path="gender">
            <n-input v-model:value="model.gender" placeholder="输入性别" />
        </n-form-item>
        <n-form-item label="国籍" path="nationality">
            <n-input v-model:value="model.nationality" placeholder="输入国籍" />
        </n-form-item>
        <n-form-item label="生日" path="birthDate">
            <n-date-picker v-model:formatted-value="model.birthDate" value-format="yyyy-MM-dd" type="date"
                style="width: 100%;" />
        </n-form-item>
        <n-form-item label="头像" path="profileImageUrl">
            <n-space vertical>
                <n-avatar v-if="model.profileImageUrl" :size="96" :src="model.profileImageUrl" round />
                <n-upload :custom-request="handleUpload" :show-file-list="false" :max="1" accept="image/*">
                    <n-button :loading="uploading">
                        <template #icon>
                            <n-icon :component="CloudUploadIcon" />
                        </template>
                        上传图片
                    </n-button>
                </n-upload>
                <n-input v-model:value="model.profileImageUrl" placeholder="或直接粘贴图片链接" />
            </n-space>
        </n-form-item>
    </n-form>
</template>

<script setup>
import { ref, watch } from 'vue';
import { NForm, NFormItem, NInput, NDatePicker, NSpace, NAvatar, NUpload, NButton, NIcon, useMessage } from 'naive-ui';
import { CloudUploadOutline as CloudUploadIcon } from '@vicons/ionicons5';
import axios from 'axios';

const props = defineProps({ modelValue: Object });
const emit = defineEmits(['update:modelValue']);

const message = useMessage();
const formRef = ref(null);
const model = ref({});
const uploading = ref(false);
const rules = {
    name: { required: true, message: '请输入姓名', trigger: 'blur' },
};

// 当外部传入的 modelValue 变化时，更新内部的 model
watch(() => props.modelValue, (newValue) => {
    model.value = { ...newValue };
}, { immediate: true, deep: true });

// 当内部的 model 变化时，通知外部
watch(model, (newValue) => {
    emit('update:modelValue', newValue);
}, { deep: true });

const handleUpload = async ({ file, onFinish, onError }) => {
    uploading.value = true;
    const formData = new FormData();
    formData.append("image", file.file);
    formData.append("key", "4312ec520960fe609d17eb3f8a99ca5e"); // 你的 ImgBB API Key

    try {
        const response = await axios.post("https://api.imgbb.com/1/upload", formData);
        model.value.profileImageUrl = response.data.data.url;
        message.success("上传成功！");
        onFinish();
    } catch (error) {
        message.error("上传失败: " + (error.response?.data?.error?.message || error.message));
        onError();
    } finally {
        uploading.value = false;
    }
};

// 暴露 validate 方法给父组件调用
const validate = () => { // <--- 移除 callback 参数
    return formRef.value.validate(); // <--- 直接返回 n-form 的 validate() Promise
};


defineExpose({ validate });
</script>