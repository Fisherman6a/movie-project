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
    login(credentials) {
        return apiClient.post('/auth/login', credentials);
    },
    register(userInfo) {
        return apiClient.post('/auth/register', userInfo);
    },

    getHotMovies(limit = 10) {
        return apiClient.get(`/movies/hot?limit=${limit}`);
    },
    getLatestMovies(limit = 10) {
        return apiClient.get(`/movies/search?sortBy=releaseYear&sortDir=desc&size=${limit}`);
    },
    searchMovies(params) {
        return apiClient.get(`/movies/${params.type}?name=${params.name}`);
    },
    // **新增**: 按标题搜索电影
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
        return apiClient.get('/movies/search?size=1000');
    },

    // 演员api
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

    // 导演管理api
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

    getAllReviews() {
        return apiClient.get('/reviews');
    },
    getReviewsForMovie(movieId) {
        return apiClient.get(`/movies/${movieId}/reviews`);
    },
    getReviewsByUserId(userId) {
        return apiClient.get(`/users/${userId}/reviews`);
    },
    addReview(movieId, userId, score, commentText) {
        return apiClient.post(`/movies/${movieId}/reviews?userId=${userId}`, { score: score * 2, commentText });
    },
    updateReview(reviewId, data) {
        return apiClient.put(`/reviews/${reviewId}`, data);
    },
    deleteReview(reviewId) {
        return apiClient.delete(`/reviews/${reviewId}`);
    },
    voteOnReview(reviewId, direction) {
        return apiClient.post(`/reviews/${reviewId}/vote`, { direction });
    },
    rateMovie(movieId, userId, score) {
        return apiClient.post(`/movies/${movieId}/ratings?userId=${userId}`, { score });
    },

    updateUserProfile(data) {
        // 注意：这里的apiClient实例会自动附加token，比直接用axios好
        return apiClient.put('/users/me', data);
    },

    getAllUsers() {
        return apiClient.get('/users');
    },
    deleteUser(id) {
        return apiClient.delete(`/users/${id}`);
    },
};