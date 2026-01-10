import axios from 'axios';
import { useAuthStore } from '@/stores/authStore';

const apiClient = axios.create({
    baseURL: 'http://localhost:7070/api',
    headers: {
        'Content-Type': 'application/json'
    }
});

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
    // 获取图形验证码
    getCaptcha() {
        return apiClient.get('/verification/captcha');
    },

    login(credentials) {
        return apiClient.post('/auth/login', credentials);
    },
    register(userInfo) {
        return apiClient.post('/auth/register', userInfo);
    },
    logout() {
        return apiClient.post('/auth/logout');
    },

    getHotMovies(limit = 10) {
        return apiClient.get(`/movies/hot?limit=${limit}`);
    },
    getLatestMovies(params) {
        return apiClient.get(`/movies/search`, { params });
    },
    searchMovies(params) {
        // 这个接口似乎是按姓名搜电影，但后端实现是按演员/导演，建议保持原样或重命名以明确
        return apiClient.get(`/movies/${params.type}?name=${params.name}`);
    },
    searchMoviesByTitle(title) {
        return apiClient.get(`/movies/search?title=${encodeURIComponent(title)}`);
    },
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
    getAllMovies() {
        return apiClient.get('/movies/search?size=1000'); // 获取所有电影用于管理后台
    },

    // 演员API
    getAllActors() {
        return apiClient.get('/actors');
    },
    getActorById(id) {
        return apiClient.get(`/actors/${id}`);
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
    searchActors(name) {
        return apiClient.get(`/actors/search?name=${encodeURIComponent(name)}`);
    },

    // 导演API
    getAllDirectors() {
        return apiClient.get('/directors');
    },
    getDirectorById(id) {
        return apiClient.get(`/directors/${id}`);
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
    searchDirectors(name) {
        return apiClient.get(`/directors/search?name=${encodeURIComponent(name)}`);
    },

    // 评论 & 评分 API
    getAllReviews() {
        return apiClient.get('/reviews');
    },
    getReviewsForMovie(movieId) {
        return apiClient.get(`/movies/${movieId}/reviews`);
    },
    getReviewsByUserId(userId) {
        return apiClient.get(`/users/${userId}/reviews`);
    },
    // **核心修改**: 合并 addReview 和 rateMovie
    addReview(movieId, userId, score, commentText) {
        // score 是 1-5 星，后端需要 1-10分
        return apiClient.post(`/movies/${movieId}/reviews?userId=${userId}`, { score: score * 2, commentText });
    },
    // **核心修改**: 更新评论时也可能需要更新分数
    updateReview(reviewId, data) { // data can be { commentText, score }
        return apiClient.put(`/reviews/${reviewId}`, data);
    },
    deleteReview(reviewId) {
        return apiClient.delete(`/reviews/${reviewId}`);
    },
    voteOnReview(reviewId, likerId, direction) {
        return apiClient.post(`/reviews/${reviewId}/vote?likerId=${likerId}`, { direction });
    },

    // 用户API
    updateUserProfile(data) {
        return apiClient.put('/users/me', data);
    },
    getAllUsers() {
        return apiClient.get('/users');
    },
    deleteUser(id) {
        return apiClient.delete(`/users/${id}`);
    },
    changePassword(passwordData) {
        return apiClient.put('/users/me/password', passwordData);
    },
    changeEmail(newEmail) {
        return apiClient.put('/users/me/email', { email: newEmail });
    },
    changePhone(newPhone) {
        return apiClient.put('/users/me/phone', { phone: newPhone });
    },

    // 通知 API
    getUserNotifications(userId) {
        return apiClient.get(`/notifications/user/${userId}`);
    },
    getUnreadNotifications(userId) {
        return apiClient.get(`/notifications/user/${userId}/unread`);
    },
    getUnreadCount(userId) {
        return apiClient.get(`/notifications/user/${userId}/unread/count`);
    },
    markNotificationAsRead(notificationId) {
        return apiClient.put(`/notifications/${notificationId}/read`);
    },
    markAllNotificationsAsRead(userId) {
        return apiClient.put(`/notifications/user/${userId}/read-all`);
    },
    deleteNotification(notificationId) {
        return apiClient.delete(`/notifications/${notificationId}`);
    },
    deleteAllReadNotifications(userId) {
        return apiClient.delete(`/notifications/user/${userId}/read`);
    },
};