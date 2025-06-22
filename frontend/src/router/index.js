import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import AdminLayout from '@/views/admin/AdminLayout.vue'
import { useAuthStore } from '@/store/auth'

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomeView
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('@/views/LoginView.vue')
        },
        {
            path: '/register',
            name: 'register',
            component: () => import('@/views/RegisterView.vue')
        },
        {
            path: '/movies',
            name: 'movies',
            component: () => import('@/views/MoviesView.vue')
        },
        {
            path: '/movie/:id',
            name: 'movie-detail',
            component: () => import('@/views/MovieDetail.vue')
        },
        {
            path: '/profile',
            name: 'profile',
            component: () => import('@/views/ProfileView.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/admin',
            component: AdminLayout,
            meta: { requiresAuth: true, requiresAdmin: true },
            children: [
                {
                    path: '',
                    name: 'admin-dashboard',
                    component: () => import('@/views/admin/DashBoard.vue')
                },
                {
                    path: 'movies',
                    name: 'admin-movies',
                    component: () => import('@/views/admin/MovieManagement.vue')
                }
            ]
        }
    ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const authStore = useAuthStore()

    if (to.meta.requiresAdmin && !authStore.isAdmin) {
        // 非管理员访问后台，跳转到首页
        next({ name: 'home' })
    }
    else if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        // 需要登录但未登录，跳转到登录页
        next({ name: 'login' })
    }
    else {
        // 其他情况直接放行
        next()
    }
})

export default router