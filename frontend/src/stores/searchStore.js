import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useSearchStore = defineStore('search', () => {
    // 定义一个全局的 isLoading 状态
    const isLoading = ref(false);

    // 定义一个 action 来开始加载
    function startLoading() {
        isLoading.value = true;
    }

    // 定义一个 action 来结束加载
    function stopLoading() {
        isLoading.value = false;
    }

    // 将状态和 actions return 出去，以便其他组件使用
    return { isLoading, startLoading, stopLoading };
});