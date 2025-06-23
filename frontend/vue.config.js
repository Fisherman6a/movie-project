const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  // 新增 devServer 配置
  devServer: {
    client: {
      overlay: {
        runtimeErrors: (error) => {
          // 当错误信息包含 "ResizeObserver loop" 时，
          // 返回 false，表示“不要在屏幕上显示这个错误”
          if (error.message.includes('ResizeObserver loop')) {
            return false;
          }
          // 对于其他所有运行时错误，保持默认行为（显示错误）
          return true;
        },
      },
    },
  },
})
