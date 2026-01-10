<template>
    <n-form ref="formRef" :model="model" :rules="rules" label-placement="left" label-width="80">
        <n-form-item label="标题" path="title">
            <n-input v-model:value="model.title" placeholder="输入电影标题" />
        </n-form-item>
        <n-form-item label="年份" path="releaseYear">
            <n-input-number v-model:value="model.releaseYear" placeholder="输入发行年份" style="width: 100%;" />
        </n-form-item>
        <n-form-item label="时长" path="duration">
            <n-input-number v-model:value="model.duration" placeholder="输入电影时长（分钟）" style="width: 100%;">
                <template #suffix>
                    分钟
                </template>
            </n-input-number>
        </n-form-item>
        <n-form-item label="类型" path="genre">
            <n-input v-model:value="model.genre" placeholder="例如：科幻, 动作" />
        </n-form-item>
        <n-form-item label="国家" path="country">
            <n-input v-model:value="model.country" placeholder="例如：美国 / 中国大陆" />
        </n-form-item>
        <n-form-item label="语言" path="language">
            <n-input v-model:value="model.language" placeholder="例如：英语" />
        </n-form-item>

        <n-form-item label="海报" path="posterUrl">
            <n-space vertical>
                <img v-if="model.posterUrl" :src="model.posterUrl" class="form-image-preview" />
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

        <n-form-item label="演员" path="actorIds">
            <n-select v-model:value="model.actorIds" multiple filterable placeholder="选择参演演员" :options="actorOptions"
                :loading="loadingActors" />
        </n-form-item>
        <n-form-item label="导演" path="directorIds">
            <n-select v-model:value="model.directorIds" multiple filterable placeholder="选择导演"
                :options="directorOptions" :loading="loadingDirectors" />
        </n-form-item>
        <n-form-item label="简介" path="synopsis">
            <n-input v-model:value="model.synopsis" type="textarea" placeholder="输入电影简介"
                :autosize="{ minRows: 3, maxRows: 5 }" />
        </n-form-item>
    </n-form>
</template>

<script setup>
// **核心修改 1**: 引入 computed
import { ref, onMounted, computed } from 'vue';
import { NForm, NFormItem, NInput, NInputNumber, NSelect, NSpace, NUpload, NButton, NIcon, useMessage } from 'naive-ui';
import { CloudUploadOutline as CloudUploadIcon } from '@vicons/ionicons5';
import apiService from '@/services/apiService';
// import axios from 'axios';  // 已改用 MinIO，不再需要 axios

const props = defineProps({
    modelValue: {
        type: Object,
        default: () => ({})
    }
});
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

const actorOptions = ref([]);
const directorOptions = ref([]);
const loadingActors = ref(false);
const loadingDirectors = ref(false);

const rules = {
    title: { required: true, message: '请输入标题', trigger: 'blur' },
    releaseYear: { type: 'number', required: true, message: '请输入年份', trigger: 'blur' },
    posterUrl: { required: true, message: '请上传海报', trigger: 'blur' },
};



const handleUpload = async ({ file, onFinish, onError }) => {
    uploading.value = true;
    const formData = new FormData();
    formData.append("file", file.file);  // MinIO 使用 "file" 字段名

    // ========== 旧代码（ImgBB）已注释 ==========
    // formData.append("image", file.file);
    // formData.append("key", "4312ec520960fe609d17eb3f8a99ca5e");
    // const response = await axios.post("https://api.imgbb.com/1/upload", formData);
    // model.value.posterUrl = response.data.data.url;
    // ==========================================

    try {
        const response = await apiService.uploadPoster(formData);
        model.value.posterUrl = response.data.url;
        message.success("上传成功！");
        onFinish();
    } catch (error) {
        message.error("上传失败: " + (error.response?.data?.message || error.message));
        onError();
    } finally {
        uploading.value = false;
    }
};

onMounted(async () => {
    loadingActors.value = true;
    loadingDirectors.value = true;
    try {
        const [actorsRes, directorsRes] = await Promise.all([
            apiService.getAllActors(),
            apiService.getAllDirectors()
        ]);
        actorOptions.value = actorsRes.data.map(a => ({ label: a.name, value: a.id }));
        directorOptions.value = directorsRes.data.map(d => ({ label: d.name, value: d.id }));
    } catch (error) {
        console.error("Failed to load actors/directors", error);
        message.error("加载演员或导演列表失败");
    } finally {
        loadingActors.value = false;
        loadingDirectors.value = false;
    }
});

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