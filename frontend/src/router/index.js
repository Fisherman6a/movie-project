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
        component: () => import('@/views/AuthView.vue'),
        props: { defaultTab: 'signin' }
    },
    {
        path: '/register',
        name: 'RegisterView',
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
    {
        path: '/profile',
        name: 'UserProfile',
        component: () => import('@/views/UserProfile.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/settings',
        name: 'AccountSettings',
        component: () => import('@/views/AccountSettings.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/browser',
        name: 'BrowserPage',
        component: () => import('@/views/BrowserPage.vue'),
        props: (route) => ({ query: route.query })
    },
    {
        path: '/search/person',
        name: 'PersonSearch',
        component: () => import('@/views/PersonSearch.vue'),
        props: route => ({ keyword: route.query.keyword })
    },
    {
        path: '/actor/:id',
        name: 'ActorDetail', // 新路由名
        component: () => import('@/views/PersonDetail.vue'),
        props: true
    },
    {
        path: '/director/:id',
        name: 'DirectorDetail', // 新路由名
        component: () => import('@/views/PersonDetail.vue'),
        props: true
    },
    {
        path: '/admin',
        name: 'AdminDashboard',
        component: () => import('@/views/AdminDashboard.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
    },
];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes,
});

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();
    if (to.meta.requiresAdmin) {
        if (authStore.isAuthenticated && authStore.user.role === 'ROLE_ADMIN') {
            next();
        } else {
            next({ name: 'Home' });
        }
    }
    else if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        next({ name: 'LoginView' });
    }
    else {
        next();
    }
});

export default router;