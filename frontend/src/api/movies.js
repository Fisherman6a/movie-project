import apiClient from './index';

// 获取热门电影
export const getHotMovies = (limit = 10) => {
    return apiClient.get(`/movies/hot?limit=${limit}`);
};

// 搜索电影（带分页和排序）
export const searchMovies = (params) => {
    return apiClient.get('/movies/search', { params });
};

// 获取单个电影详情
export const getMovieById = (id) => {
    return apiClient.get(`/movies/${id}`);
};

// --- 以下为管理员权限 ---

// 创建电影
export const createMovie = (movieData) => {
    return apiClient.post('/movies', movieData);
};

// 更新电影
export const updateMovie = (id, movieData) => {
    return apiClient.put(`/movies/${id}`, movieData);
};

// 删除电影
export const deleteMovie = (id) => {
    return apiClient.delete(`/movies/${id}`);
};

export const getLatestMovies = (limit = 6) => {
    const params = {
        sortBy: 'releaseYear', // 按发行年份排序
        sortDir: 'desc',       // 降序（最新的在前）
        page: 0,
        size: limit
    };
    return apiClient.get('/movies/search', { params });
  };