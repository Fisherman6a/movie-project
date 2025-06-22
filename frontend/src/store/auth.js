import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { login as apiLogin, register as apiRegister } from '@/api/auth';
import router from '@/router';

export const useAuthStore = defineStore('auth', () => {
    // state
    const user = ref(null);
    const token = ref(null);
    const role = ref(null);
    const userId = ref(null);

    // getters
    const isAuthenticated = computed(() => !!token.value);
    const isAdmin = computed(() => role.value === 'ROLE_ADMIN');

    // actions
    async function login(credentials) {
        try {
            const response = await apiLogin(credentials);
            const data = response.data;
            token.value = data.token;
            user.value = { username: data.username };
            role.value = data.role;
            userId.value = data.userId;

            // 登录成功后跳转到首页
            router.push('/');
        } catch (error) {
            console.error('登录失败:', error);
            throw error;
        }
    }

    async function register(userData) {
        try {
            await apiRegister(userData);
            // 注册成功后可以引导用户去登录
            router.push('/login');
        } catch (error) {
            console.error('注册失败:', error);
            throw error;
        }
    }

    function logout() {
        user.value = null;
        token.value = null;
        role.value = null;
        userId.value = null;
        router.push('/login');
    }

    return {
        user,
        token,
        role,
        userId,
        isAuthenticated,
        isAdmin,
        login,
        register,
        logout,
    };
}, {
    persist: true, // 开启持久化，将 state 保存到 localStorage
});