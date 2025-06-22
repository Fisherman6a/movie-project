// 文件: src/router/index.js

import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';
import HomePage from '@/views/HomePage.vue';

const routes = [
    {
        path: '/',
        name: 'Home',
        component: HomePage,
    },
    {
        path: '/login',
        name: 'LoginView',
        // 确保这里指向的是 AuthView.vue
        component: () => import('@/views/AuthView.vue'),
        props: { defaultTab: 'signin' }
    },
    {
        path: '/register',
        name: 'RegisterView',
        // 确保这里也指向的是 AuthView.vue
        component: () => import('@/views/AuthView.vue'),
        props: { defaultTab: 'signup' }
    },
    {
        path: '/movies/:id',
        name: 'MovieDetail',
        component: () => import('@/views/MovieDetail.vue'),
        props: true
    },
    {
        path: '/search',
        name: 'MovieSearch',
        component: () => import('@/views/MovieSearch.vue'),
        props: route => ({ keyword: route.query.keyword })
    },
    {
        path: '/my-reviews',
        name: 'UserReviews',
        component: () => import('@/views/UserReviews.vue'),
        meta: { requiresAuth: true }
    },
];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes,
});

// 全局前置守卫 (保持不变)
router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();
    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        next({ name: 'LoginView' });
    } else {
        next();
    }
});

export default router;