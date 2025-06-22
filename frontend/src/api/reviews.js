import apiClient from './index';

// 获取电影的评论
export const getReviewsForMovie = (movieId) => {
    return apiClient.get(`/movies/${movieId}/reviews`);
};

// 为电影添加评论
// 后端需要 userId，我们从 Pinia store 中获取
export const addReview = (movieId, userId, commentText) => {
    return apiClient.post(`/movies/${movieId}/reviews?userId=${userId}`, { commentText });
};

// 添加或更新评分
export const addOrUpdateRating = (movieId, userId, score) => {
    return apiClient.post(`/movies/${movieId}/ratings?userId=${userId}`, { score });
};

// 删除评论
export const deleteReview = (reviewId) => {
    return apiClient.delete(`/reviews/${reviewId}`);
};