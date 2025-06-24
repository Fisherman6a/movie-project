<template>
    <n-layout-content>
        <n-spin :show="loading">
            <div v-if="person">
                <n-h1>{{ person.name }}</n-h1>
                <n-avatar :size="120" :src="person.profileImageUrl" />
                <p>国籍: {{ person.nationality }}</p>
                <p>性别: {{ person.gender }}</p>
                <p>生日: {{ person.birthDate }}</p>
                <n-h2>参演/执导的电影</n-h2>
                <p>（此部分功能待实现）</p>
            </div>
            <n-empty v-else description="人物信息加载失败或不存在。" />
        </n-spin>
    </n-layout-content>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { NLayoutContent, NSpin, NH1, NH2, NAvatar, NEmpty, useMessage } from 'naive-ui';
import apiService from '@/services/apiService';

const props = defineProps({
    id: String
});

const person = ref(null);
const loading = ref(true);
const message = useMessage();

const fetchPersonData = async (personId) => {
    loading.value = true;
    try {
        // 尝试作为演员获取，如果失败再作为导演获取
        const response = await apiService.getActorById(personId).catch(() => apiService.getDirectorById(personId));
        person.value = response.data;
    } catch (error) {
        message.error("加载人物信息失败");
        console.error(error);
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
    fetchPersonData(props.id);
});

// 监听 id 变化，以便在同一页面内跳转时重新加载数据
watch(() => props.id, (newId) => {
    fetchPersonData(newId);
});
</script>