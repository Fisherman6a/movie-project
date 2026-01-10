import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

class WebSocketService {
  constructor() {
    this.client = null;
    this.connected = false;
    this.userId = null;
    this.subscriptions = new Map();
    this.reconnectAttempts = 0;
    this.maxReconnectAttempts = 5;
    this.reconnectDelay = 3000;
  }

  /**
   * 连接 WebSocket
   * @param {Number} userId - 用户ID
   * @param {Function} onNotification - 接收通知的回调函数
   */
  connect(userId, onNotification) {
    if (this.connected) {
      console.log('WebSocket 已连接');
      return;
    }

    this.userId = userId;

    // 创建 SockJS 连接
    const socket = new SockJS('http://localhost:7070/ws');

    // 创建 STOMP 客户端
    this.client = new Client({
      webSocketFactory: () => socket,
      debug: (str) => {
        console.log('STOMP Debug:', str);
      },
      reconnectDelay: this.reconnectDelay,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    // 连接成功回调
    this.client.onConnect = (frame) => {
      console.log('WebSocket 连接成功:', frame);
      this.connected = true;
      this.reconnectAttempts = 0;

      // 订阅用户专属通知队列
      const subscription = this.client.subscribe(
        `/user/${userId}/queue/notifications`,
        (message) => {
          try {
            const notification = JSON.parse(message.body);
            console.log('收到通知:', notification);

            // 调用回调函数处理通知
            if (onNotification && typeof onNotification === 'function') {
              onNotification(notification);
            }
          } catch (error) {
            console.error('解析通知消息失败:', error);
          }
        }
      );

      this.subscriptions.set('notifications', subscription);
    };

    // 连接错误回调
    this.client.onStompError = (frame) => {
      console.error('STOMP 错误:', frame);
      this.connected = false;
      this.handleReconnect(userId, onNotification);
    };

    // WebSocket 错误回调
    this.client.onWebSocketError = (event) => {
      console.error('WebSocket 错误:', event);
      this.connected = false;
    };

    // WebSocket 关闭回调
    this.client.onWebSocketClose = (event) => {
      console.log('WebSocket 连接关闭:', event);
      this.connected = false;
      this.handleReconnect(userId, onNotification);
    };

    // 激活连接
    this.client.activate();
  }

  /**
   * 处理重连逻辑
   */
  handleReconnect(userId, onNotification) {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      console.log(`尝试重连 (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);

      setTimeout(() => {
        this.connect(userId, onNotification);
      }, this.reconnectDelay);
    } else {
      console.error('达到最大重连次数，停止重连');
    }
  }

  /**
   * 断开 WebSocket 连接
   */
  disconnect() {
    if (this.client && this.connected) {
      // 取消所有订阅
      this.subscriptions.forEach((subscription) => {
        subscription.unsubscribe();
      });
      this.subscriptions.clear();

      // 断开连接
      this.client.deactivate();
      this.connected = false;
      this.userId = null;
      console.log('WebSocket 已断开');
    }
  }

  /**
   * 发送消息到服务器（如果需要）
   * @param {String} destination - 目标地址
   * @param {Object} message - 消息内容
   */
  send(destination, message) {
    if (this.client && this.connected) {
      this.client.publish({
        destination: destination,
        body: JSON.stringify(message),
      });
    } else {
      console.error('WebSocket 未连接，无法发送消息');
    }
  }

  /**
   * 检查连接状态
   */
  isConnected() {
    return this.connected;
  }
}

// 导出单例
export default new WebSocketService();
