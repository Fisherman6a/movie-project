<template>
    <div style="margin-top: 60px;">
        <n-spin :show="loading">
            <div v-if="person">
                <n-space align="start" :size="24" style="margin-bottom: 30px;">
                    <img :src="person.profileImageUrl" class="person-profile-img" />
                    <n-space vertical>
                        <n-h1 style="margin: 0;">{{ person.name }}</n-h1>
                        <n-p>国籍: {{ person.nationality || '未知' }}</n-p>
                        <n-p>性别: {{ genderText }}</n-p>
                        <n-p>生日: {{ person.birthDate || '未知' }}</n-p>
                    </n-space>
                </n-space>

                <n-h2>人物简介</n-h2>
                <div v-if="person.biography" style="margin-bottom: 30px;">
                    <n-p class="bio-text">{{ person.biography }}</n-p>
                </div>
                <n-empty v-else description="影人简介未被添加" style="margin-bottom: 30px;"></n-empty>

                <n-h2>相关电影</n-h2>
                <n-grid v-if="movies.length > 0" :x-gap="16" :y-gap="24" :cols="'2 s:3 m:4 l:5 xl:6'" responsive="true">
                    <n-grid-item v-for="movie in movies" :key="movie.id">
                        <router-link :to="{ name: 'MovieDetail', params: { id: movie.id } }">
                            <n-card :title="movie.title" hoverable content-style="padding:0;">
                                <template #cover>
                                    <img :src="movie.posterUrl" class="movie-poster-img" />
                                </template>
                                <template #footer>
                                    <n-rate readonly :value="movie.averageRating / 2" allow-half size="small" />
                                </template>
                            </n-card>
                        </router-link>
                    </n-grid-item>
                </n-grid>
                <n-empty v-else description="暂无相关电影作品。"></n-empty>
            </div>
            <n-empty v-else description="人物信息加载失败或不存在。" style="padding: 48px 0;" />
        </n-spin>
    </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'; // 引入 computed
import { useRoute } from 'vue-router';
import {
    NSpin, NH1, NH2, NP, NEmpty, useMessage, NSpace, NGrid, NGridItem, NCard, NRate
} from 'naive-ui';
import apiService from '@/services/apiService';

const props = defineProps({
    id: String
});

const route = useRoute();
const person = ref(null);
const movies = ref([]);
const loading = ref(true);
const message = useMessage();

// **核心修改**: 新增计算属性，用于转换性别显示文本
const genderText = computed(() => {
    if (!person.value?.gender) {
        return '未知';
    }
    switch (person.value.gender) {
        case 'MALE':
            return '男';
        case 'FEMALE':
            return '女';
        case 'OTHER':
            return '其他';
        default:
            return person.value.gender; // 如果是未知值，直接显示
    }
});

const fetchPersonData = async (personId) => {
    loading.value = true;
    person.value = null;
    movies.value = [];
    try {
        let personResponse;

        if (route.name === 'ActorDetail') {
            personResponse = await apiService.getActorById(personId);
        } else if (route.name === 'DirectorDetail') {
            personResponse = await apiService.getDirectorById(personId);
        } else {
            throw new Error('Unknown person route type');
        }

        person.value = personResponse.data;

        if (person.value && person.value.name) {
            const [actorMoviesRes, directorMoviesRes] = await Promise.all([
                apiService.searchMovies({ type: 'by-actor', name: person.value.name }),
                apiService.searchMovies({ type: 'by-director', name: person.value.name })
            ]);

            const allMovies = [...actorMoviesRes.data, ...directorMoviesRes.data];
            movies.value = Array.from(new Map(allMovies.map(m => [m.id, m])).values());
        }

    } catch (error) {
        person.value = null;
        message.error("加载人物信息失败");
        console.error(error);
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
    fetchPersonData(props.id);
});

watch(() => props.id, (newId) => {
    if (newId) {
        fetchPersonData(newId);
    }
});
</script>

<style scoped>
.person-profile-img {
    width: 150px;
    height: 222px;
    object-fit: cover;
    border-radius: 6px;
    background-color: #333;
}

.movie-poster-img {
    width: 100%;
    aspect-ratio: 2 / 3;
    object-fit: cover;
}

:deep(.n-card-header__main) {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

a {
    text-decoration: none;
    color: inherit;
}

.bio-text {
    line-height: 1.7;
    white-space: pre-wrap;
    font-size: 14px;
    color: rgba(255, 255, 255, 0.82);
}
</style>