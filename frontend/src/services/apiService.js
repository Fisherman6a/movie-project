import axios from 'axios';
import { useAuthStore } from '@/stores/authStore';

// 创建一个 Axios 实例
const apiClient = axios.create({
    baseURL: 'http://localhost:7070/api', // 您的后端 API 地址
    headers: {
        'Content-Type': 'application/json'
    }
});

// 添加一个请求拦截器，用于在每个请求的 header 中附加 Token
apiClient.interceptors.request.use(config => {
    const authStore = useAuthStore();
    const token = authStore.token;
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, error => {
    return Promise.reject(error);
});

export default {
    // 认证相关
    login(credentials) {
        return apiClient.post('/auth/login', credentials);
    },
    register(userInfo) {
        return apiClient.post('/auth/register', userInfo);
    },

    // 电影相关
    getHotMovies(limit = 10) {
        return apiClient.get(`/movies/hot?limit=${limit}`);
    },
    searchMovies(params) {
        // params 可以是 { name: 'keyword' }
        return apiClient.get(`/movies/${params.type}?name=${params.name}`);
    },
    getMovieById(id) {
        return apiClient.get(`/movies/${id}`);
    },

    // 评论相关
    getReviewsForMovie(movieId) {
        return apiClient.get(`/movies/${movieId}/reviews`);
    },
    getReviewsByUserId(userId) {
        return apiClient.get(`/users/${userId}/reviews`);
    },
    addReview(movieId, userId, commentText) {
        return apiClient.post(`/movies/${movieId}/reviews?userId=${userId}`, { commentText });
    },
    updateReview(reviewId, commentText) {
        return apiClient.put(`/reviews/${reviewId}`, { commentText });
    },
    deleteReview(reviewId) {
        return apiClient.delete(`/reviews/${reviewId}`);
    },

    // 评分相关
    rateMovie(movieId, userId, score) {
        return apiClient.post(`/movies/${movieId}/ratings?userId=${userId}`, { score });
    }
};