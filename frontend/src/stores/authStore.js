import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useAuthStore = defineStore('auth', () => {
    // 从 localStorage 初始化 state
    const token = ref(localStorage.getItem('token') || null);
    const user = ref(JSON.parse(localStorage.getItem('user')) || null);

    const isAuthenticated = computed(() => !!token.value);
    const username = computed(() => user.value?.username);
    const userId = computed(() => user.value?.userId);
    const profileImageUrl = computed(() => user.value?.profileImageUrl);


    function setAuth(authData) {
        token.value = authData.token;
        user.value = {
            userId: authData.userId,
            username: authData.username,
            role: authData.role,
            profileImageUrl: authData.profileImageUrl,
            bio: authData.bio,
            personalWebsite: authData.personalWebsite,
            birthDate: authData.birthDate,
            email: authData.email,
            createdAt: authData.createdAt // <-- 新增此字段
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

    return { token, user, isAuthenticated, username, userId, profileImageUrl, setAuth, clearAuth };
});