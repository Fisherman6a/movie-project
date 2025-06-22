import apiClient from './index';

export const login = (credentials) => {
    return apiClient.post('/auth/login', credentials);
};

export const register = (userData) => {
    return apiClient.post('/auth/register', userData);
};

export const getMyProfile = () => {
    return apiClient.get('/users/me');
};