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
    // ... 其他路由保持不变 ...
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
        path: '/person/:id', // 使用动态参数 id
        name: 'PersonDetail',
        component: () => import('@/views/PersonDetail.vue'),
        props: true // 将路由参数作为 props 传递给组件
    },
    {
        path: '/admin',
        name: 'AdminDashboard',
        component: () => import('@/views/AdminDashboard.vue'),
        meta: { requiresAuth: true, requiresAdmin: true } // 需要登录且需要是管理员
    },
];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes,
});

// ========== START: 修改全局前置守卫 ==========
router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();

    // 检查是否需要管理员权限
    if (to.meta.requiresAdmin) {
        if (authStore.isAuthenticated && authStore.user.role === 'ROLE_ADMIN') {
            next(); // 是管理员，放行
        } else {
            // 不是管理员，重定向到首页或显示无权限页面
            // 为简单起见，我们重定向到首页
            next({ name: 'Home' });
        }
    }
    // 检查是否需要普通登录权限
    else if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        next({ name: 'LoginView' }); // 未登录，跳转到登录页
    }
    // 其他情况
    else {
        next(); // 不需要任何权限，或已满足普通登录权限，直接放行
    }
});
// ========== END: 修改全局前置守卫 ==========

export default router;