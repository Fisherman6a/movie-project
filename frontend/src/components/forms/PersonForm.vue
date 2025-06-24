<template>
    <n-form ref="formRef" :model="model" :rules="rules" label-placement="left" label-width="80">
        <n-form-item label="姓名" path="name">
            <n-input v-model:value="model.name" placeholder="输入姓名" />
        </n-form-item>
        <n-form-item label="性别" path="gender">
            <n-select v-model:value="model.gender" placeholder="选择性别" :options="genderOptions" />
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
                <img v-if="model.profileImageUrl" :src="model.profileImageUrl" class="form-image-preview" />
                <n-upload :custom-request="handleUpload" :show-file-list="false" :max="1" accept="image/*">
                    <n-button :loading="uploading">
                        <template #icon>
                            <n-icon :component="CloudUploadIcon" />
                        </template>
                        上传图片
                    </n-button>
                </n-upload>
            </n-space>
        </n-form-item>
        <n-form-item label="简介" path="biography">
            <n-input v-model:value="model.biography" type="textarea" placeholder="输入人物简介..."
                :autosize="{ minRows: 4, maxRows: 8 }" />
        </n-form-item>
    </n-form>
</template>

<script setup>
import { ref, computed } from 'vue';
import { NForm, NFormItem, NInput, NDatePicker, NSpace, NUpload, NButton, NIcon, useMessage, NSelect } from 'naive-ui';
import { CloudUploadOutline as CloudUploadIcon } from '@vicons/ionicons5';
import axios from 'axios';

const props = defineProps({ modelValue: Object });
const emit = defineEmits(['update:modelValue']);

const message = useMessage();
const formRef = ref(null);
const uploading = ref(false);

const model = computed({
    get: () => props.modelValue,
    set: (value) => {
        emit('update:modelValue', value);
    }
});

// **核心修改**: 定义下拉框选项
const genderOptions = [
    { label: '男', value: 'MALE' },
    { label: '女', value: 'FEMALE' },
    { label: '其他', value: 'OTHER' }
];

const rules = {
    name: { required: true, message: '请输入姓名', trigger: 'blur' },
    profileImageUrl: { required: true, message: '请上传头像', trigger: 'blur' },
};

const handleUpload = async ({ file, onFinish, onError }) => {
    uploading.value = true;
    const formData = new FormData();
    formData.append("image", file.file);
    formData.append("key", "4312ec520960fe609d17eb3f8a99ca5e");

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

const validate = () => {
    return formRef.value.validate();
};

defineExpose({ validate });
</script>

<style scoped>
.form-image-preview {
    width: 96px;
    height: 142px;
    object-fit: cover;
    border-radius: 3px;
    background-color: #333;
}
</style>