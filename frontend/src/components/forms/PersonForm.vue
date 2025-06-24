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
        <n-form-item label="头像URL" path="profileImageUrl">
            <n-input v-model:value="model.profileImageUrl" placeholder="输入头像图片链接" />
        </n-form-item>
    </n-form>
</template>

<script setup>
import { ref, watch } from 'vue';
import { NForm, NFormItem, NInput, NDatePicker } from 'naive-ui';

const props = defineProps({
    modelValue: {
        type: Object,
        default: () => ({})
    }
});
const emit = defineEmits(['update:modelValue']);

const formRef = ref(null);
const model = ref({});

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

// 暴露 validate 方法给父组件调用
const validate = (callback) => {
    formRef.value.validate(callback);
};

defineExpose({ validate });
</script>