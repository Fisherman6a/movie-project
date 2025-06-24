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
    // 新增：获取最新电影的接口
    getLatestMovies(limit = 10) {
        // 假设后端通过 sortBy=releaseYear&sortDir=desc 来实现
        return apiClient.get(`/movies/search?sortBy=releaseYear&sortDir=desc&size=${limit}`);
    },
    searchMovies(params) {
        // params 可以是 { name: 'keyword' }
        return apiClient.get(`/movies/${params.type}?name=${params.name}`);
    },
    // 电影管理api
    getMovieById(id) {
        return apiClient.get(`/movies/${id}`);
    },
    createMovie(data) {
        return apiClient.post('/movies', data);
    },
    updateMovie(id, data) {
        return apiClient.put(`/movies/${id}`, data);
    },
    deleteMovie(id) {
        return apiClient.delete(`/movies/${id}`);
    },
    getAllMovies() { // 用于在表单中选择电影
        return apiClient.get('/movies/search?size=1000'); // 获取足够多的电影
    },

    // 演员管理API
    getAllActors() {
        return apiClient.get('/actors');
    },
    createActor(data) {
        return apiClient.post('/actors', data);
    },
    updateActor(id, data) {
        return apiClient.put(`/actors/${id}`, data);
    },
    deleteActor(id) {
        return apiClient.delete(`/actors/${id}`);
    },

    // 导演管理api
    getAllDirectors() {
        return apiClient.get('/directors');
    },
    createDirector(data) {
        return apiClient.post('/directors', data);
    },
    updateDirector(id, data) {
        return apiClient.put(`/directors/${id}`, data);
    },
    deleteDirector(id) {
        return apiClient.delete(`/directors/${id}`);
    },

    // 评论相关
    getAllReviews() {
        return apiClient.get('/reviews');
    },
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
    },
    // 新增用户相关接口
    sendEmailVerificationCode(data) {
        return axios.post('/api/send-email-code', data); // 替换为你的后端接口
    },
    sendPhoneVerificationCode(data) {
        return axios.post('/api/send-phone-code', data); // 替换为你的后端接口
    },
    changeEmail(data) {
        return axios.post('/api/change-email', data); // 替换为你的后端接口
    },
    changePhone(data) {
        return axios.post('/api/change-phone', data); // 替换为你的后端接口
    },
    updateUserProfile(data) {
        return axios.put('/api/user/profile', data);
    },

    //用户管理api
    getAllUsers() {
        return apiClient.get('/users');
    },
    deleteUser(id) {
        return apiClient.delete(`/users/${id}`);
    },
};