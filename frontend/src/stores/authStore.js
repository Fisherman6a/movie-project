import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useAuthStore = defineStore('auth', () => {
    // 从 localStorage 初始化 state
    const token = ref(localStorage.getItem('token') || null);
    const user = ref(JSON.parse(localStorage.getItem('user')) || null);

    const isAuthenticated = computed(() => !!token.value);
    const username = computed(() => user.value?.username);
    const userId = computed(() => user.value?.userId);

    function setAuth(authData) {
        token.value = authData.token;
        user.value = {
            userId: authData.userId,
            username: authData.username,
            role: authData.role
        };
        // 将认证信息存入 localStorage
        localStorage.setItem('token', authData.token);
        localStorage.setItem('user', JSON.stringify(user.value));
    }

    function clearAuth() {
        token.value = null;
        user.value = null;
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    }

    return { token, user, isAuthenticated, username, userId, setAuth, clearAuth };
});