<template>
  <footer class="site-footer">
    <div class="footer-container">
      <div class="footer-column">
        <h4>关于我们</h4>
        <p class="footer-about">
          “影评时光”致力于提供最新最全的电影资讯、评分和评论，打造属于影迷的交流社区。
        </p>
        <div class="footer-social-icons">
          <a href="#" title="微博">W</a>
          <a href="#" title="微信">C</a>
          <a href="#" title="GitHub">G</a>
        </div>
      </div>

      <div class="footer-column">
        <h4>快速链接</h4>
        <ul>
          <li><router-link to="/">首页</router-link></li>
          <li>
            <router-link to="/browser?quickFilter=hot">热门电影</router-link>
          </li>
          <li><router-link to="/browser?genre=科幻">科幻专区</router-link></li>
          <li><router-link to="/browser?genre=喜剧">喜剧专区</router-link></li>
        </ul>
      </div>

      <div class="footer-column">
        <h4>客户服务</h4>
        <ul>
          <li><a href="#">帮助中心</a></li>
          <li><a href="#">联系我们</a></li>
          <li><a href="#">服务条款</a></li>
          <li><a href="#">隐私政策</a></li>
        </ul>
      </div>

      <div class="footer-column">
        <h4>联系我们</h4>
        <p>
          客服电话：400-123-4567<br />
          电子邮箱：service@movie-time.com<br />
          工作时间：7x24小时
        </p>
      </div>
    </div>

    <div class="footer-bottom">
      <p>&copy; 2024 影评时光 版权所有</p>
    </div>
  </footer>
</template>

<script setup>
// 目前这个组件没有复杂的逻辑，所以 script 部分为空
</script>

<style scoped>
.site-footer {
  /* **核心修改**: 增加顶部外边距，与页面内容隔开 */
  margin-top: 30px;

  background-color: #18181c;
  color: rgba(255, 255, 255, 0.52);
  padding: 40px 0;
  font-size: 14px;
  border-top: 1px solid rgba(255, 255, 255, 0.09);
}

.footer-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer-column {
  width: 22%;
  min-width: 200px;
  margin-bottom: 20px;
}

.footer-column h4 {
  color: #ffffff;
  font-size: 16px;
  margin-bottom: 15px;
}

.footer-column ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-column ul li {
  margin-bottom: 10px;
}

.footer-column a,
.footer-column p {
  color: rgba(255, 255, 255, 0.52);
  text-decoration: none;
  transition: color 0.3s;
}

.footer-column a:hover {
  color: #ffffff;
}

.footer-about {
  line-height: 1.6;
}

.footer-social-icons a {
  display: inline-block;
  width: 30px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.09);
  color: #ffffff;
  margin-top: 10px;
  margin-right: 10px;
  font-weight: bold;
  transition: background-color 0.3s;
}

.footer-social-icons a:hover {
  background-color: #36ad6a;
  text-decoration: none;
}

.footer-bottom {
  text-align: center;
  border-top: 1px solid rgba(255, 255, 255, 0.09);
  padding-top: 20px;
  margin-top: 20px;
  color: rgba(255, 255, 255, 0.38);
}
</style>