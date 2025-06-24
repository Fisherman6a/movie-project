<template>
    <div>
        <n-h2>
            搜索影人“<n-text type="primary">{{ keyword }}</n-text>”的结果
        </n-h2>

        <n-spin :show="isLoading">
            <div v-if="actors.length > 0" class="results-category">
                <n-h3 prefix="bar" type="info">相关演员</n-h3>
                <n-list bordered>
                    <n-list-item v-for="person in actors" :key="person.id">
                        <router-link :to="{ name: 'ActorDetail', params: { id: person.id } }" class="person-item-link">
                            <n-space align="center">
                                <img :src="person.profileImageUrl" class="person-list-img" />
                                <n-text strong>{{ person.name }}</n-text>
                                <n-text depth="3">{{ person.nationality }}</n-text>
                            </n-space>
                        </router-link>
                    </n-list-item>
                </n-list>
            </div>

            <div v-if="directors.length > 0" class="results-category">
                <n-h3 prefix="bar" type="success">相关导演</n-h3>
                <n-list bordered>
                    <n-list-item v-for="person in directors" :key="person.id">
                        <router-link :to="{ name: 'DirectorDetail', params: { id: person.id } }"
                            class="person-item-link">
                            <n-space align="center">
                                <img :src="person.profileImageUrl" class="person-list-img" />
                                <n-text strong>{{ person.name }}</n-text>
                                <n-text depth="3">{{ person.nationality }}</n-text>
                            </n-space>
                        </router-link>
                    </n-list-item>
                </n-list>
            </div>

            <n-empty v-if="!isLoading && actors.length === 0 && directors.length === 0" description="没有找到相关的影人。">
            </n-empty>
        </n-spin>
    </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { useMessage, NSpin, NH2, NH3, NText, NList, NListItem, NSpace, NEmpty } from 'naive-ui';
import apiService from '@/services/apiService';

const props = defineProps({
    keyword: String,
});

const message = useMessage();
const isLoading = ref(true);
const actors = ref([]);
const directors = ref([]);

const performSearch = async (name) => {
    if (!name) return;

    isLoading.value = true;
    actors.value = [];
    directors.value = [];

    try {
        const [actorsRes, directorsRes] = await Promise.all([
            apiService.searchActors(name),
            apiService.searchDirectors(name)
        ]);
        actors.value = actorsRes.data;
        directors.value = directorsRes.data;
    } catch (error) {
        message.error('搜索影人时出错，请稍后重试。');
        console.error('搜索影人时出错:', error);
    } finally {
        isLoading.value = false;
    }
};

onMounted(() => {
    performSearch(props.keyword);
});

watch(() => props.keyword, (newKeyword) => {
    performSearch(newKeyword);
});
</script>

<style scoped>
.results-category {
    margin-bottom: 40px;
}

.person-item-link {
    text-decoration: none;
    color: inherit;
    display: block;
    padding: 8px;
    transition: background-color 0.3s;
}

.person-item-link:hover {
    background-color: rgba(255, 255, 255, 0.05);
}

.person-list-img {
    width: 60px;
    height: 88px;
    object-fit: cover;
    border-radius: 3px;
    background-color: #333;
}
</style>