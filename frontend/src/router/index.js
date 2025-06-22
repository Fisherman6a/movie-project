// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/views/HomePage.vue';

const routes = [
    {
        path: '/',
        name: 'Home',
        component: HomePage,
    },
    // You can add more routes here later
    // {
    //   path: '/about',
    //   name: 'About',
    //   component: () => import('../views/About.vue') // Lazy loading
    // }
];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes,
});

export default router;