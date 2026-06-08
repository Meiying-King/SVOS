<!-- 父组件:除登录页以外其它组件的父元素
     负责其它二级页面的公共部分(左侧菜单栏+顶部条)-->
<template>
  <div style="display: flex;">
    <!-- 左侧菜单栏 折叠:64px 不折叠:208px   -->
    <div :style="{width:(isCollapse ? '64px' : '208px')}">
      <el-menu
          style="width:100%;height: 100vh;"
          background-color="#3c82f5"
          text-color="#fff"
          active-text-color="#ff0"
          unique-opened
          :collapse="isCollapse"
          :collapse-transition="false"
          router
      >
        <!-- 左侧菜单栏顶部条 -->
        <el-row style="padding-top: 10px;">
          <el-col :span="2"></el-col>
          <el-col :span="7" style="padding-left: 7px;">
            <el-avatar src="/imgs/admin/logo.png"></el-avatar>
          </el-col>
          <el-col :span="isCollapse ? 0 : 13">
            <span style="color:#fff;font-weight: bold;position:relative;top:8px;left: 2px;">
              智慧车辆</span>
          </el-col>
        </el-row>
        <el-sub-menu index="1">
          <template #title>
            <el-icon><User/></el-icon><span>用户管理</span>
          </template>
          <el-menu-item index="/user">用户列表</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="2">
          <template #title>
            <el-icon><Van/></el-icon><span>车辆管理</span>
          </template>
          <el-menu-item index="/vehicle">车辆列表</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="3">
          <template #title>
            <el-icon><Location/></el-icon><span>围栏管理</span>
          </template>
          <el-menu-item index="/geofence">围栏列表</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="4">
          <template #title>
            <el-icon><Place/></el-icon><span>调度管理</span>
          </template>
          <el-menu-item index="/application">用车申请</el-menu-item>
          <el-menu-item index="/audit">用车审批</el-menu-item>
          <el-menu-item index="/distribute">车辆分配</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="5">
          <template #title>
            <el-icon><MessageBox/></el-icon><span>字典管理</span>
          </template>
          <el-menu-item index="dict">字典列表</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </div>
    <!-- 右侧主体内容 占除左侧以外的全部宽度  -->
    <div :style="{width:(isCollapse ? 'calc(100% - 64px)' : 'calc(100% - 208px)')}">
      <!-- 右侧顶部条  -->
      <el-header style="height:10vh;background-color: #fff;padding-top: 22px;">
        <el-row>
          <el-col :span="18">
            <el-icon style="font-size: 21px;" @click="changeCollapsed"><Expand/></el-icon>
          </el-col>
          <el-col :span="6" style="position: relative;">
            <el-dropdown trigger="click" style="position: absolute;right: 20px;">
              <span style="font-size: 19px;font-weight: bold;">
                {{user.username}}
                <el-icon><arrow-down/></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </el-col>
        </el-row>
        <!-- 面包屑导航 -->
        <el-breadcrumb separator="/" style="position: relative;top: 20px;">
          <el-breadcrumb-item>首页</el-breadcrumb-item>
          <el-breadcrumb-item v-for="item in breadcrumb">{{item}}</el-breadcrumb-item>
        </el-breadcrumb>
      </el-header>
      <!-- 右侧主体内容  -->
      <el-main style="height: 90vh;padding: 0;overflow-y: auto;">
        <!--  里面是可变区域 (各个子组件) -->
        <router-view/>
      </el-main>
      <!-- 智能客服小图标 - 半隐藏在右边缘 -->
      <el-button
          v-show="!visibleDrawer"
          type="info"
          circle
          class="chat-icon-button"
          :class="{ 'chat-icon-show': isChatIconVisible }"
          style="position: fixed;right: -40px; bottom: 200px; z-index: 9999; width: 64px; height: 64px; transition: right 0.3s ease;"
          size="1000px"
          @mouseenter="isChatIconVisible = true"
          @mouseleave="isChatIconVisible = false"
          @click="CustommerChat"
      >
        <el-icon :size="40"><ChatDotRound /></el-icon>
      </el-button>
    </div>
  </div>

  <!-- 智能客服对话抽屉 -->
  <el-drawer
      v-model="visibleDrawer"
      direction="rtl"
      :before-close="handleClose"
      size="33%"
      :with-header="true"
  >
    <template #header>
      <div style="display: flex; align-items: center; justify-content: space-between; width: 100%;">
        <span style="font-size: 18px; font-weight: bold;">智能客服</span>
      </div>
    </template>
    
    <!-- 对话消息列表 -->
    <div class="chat-container">
      <div class="message-list" ref="messageListRef">
        <div 
          v-for="(msg, index) in messageList" 
          :key="index" 
          :class="['message-item', msg.role === 'user' ? 'message-user' : 'message-bot']"
        >
          <div class="message-avatar">
            <el-avatar 
              v-if="msg.role === 'bot'" 
              src="/imgs/admin/logo.png" 
              :size="40"
            />
            <el-avatar 
              v-else 
              :size="40"
            >
              <el-icon><User /></el-icon>
            </el-avatar>
          </div>
          <div class="message-content">
            <div class="message-bubble">
              {{ msg.content }}
            </div>
            <div class="message-time">
              {{ msg.time }}
            </div>
          </div>
        </div>
        <!-- 加载中提示 -->
        <div v-if="isLoading" class="message-item message-bot">
          <div class="message-avatar">
            <el-avatar src="/imgs/admin/logo.png" :size="40" />
          </div>
          <div class="message-content">
            <div class="message-bubble loading">
              <span class="dot">正在输入</span>
              <span class="dots">...</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 输入框区域 -->
      <div class="input-area">
        <el-input
          v-model="userMessage"
          type="textarea"
          :rows="3"
          placeholder="请输入您的问题..."
          @keydown.enter.prevent="sendMessage"
          :disabled="isSending"
        />
        <el-button 
          type="primary" 
          @click="sendMessage" 
          :disabled="!userMessage.trim() || isSending"
          style="width: 100%; margin-top: 10px;"
        >
          发送
        </el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script setup>
import {onMounted, ref, nextTick} from "vue";
import {useRoute, useRouter} from "vue-router";
import axios from "axios";
import { ElMessage } from "element-plus";

//定义变量用来控制左侧菜单栏是否折叠
const isCollapse = ref(false);
//定义修改左侧菜单栏折叠状态的方法
const changeCollapsed = ()=>{
  isCollapse.value = !isCollapse.value;
}
//获取当前登录人数据
const user = ref(getUser());
//定义退出登录的方法
const logout = ()=>{
  if(confirm('您确认要退出登录吗?')){
    //退出时要清空登录的用户数据
    localStorage.removeItem('user');
    //跳转到登录页
    window.location.href = '/login';
    //还需要把已经取出来的 user 对象清空
    user.value = '';
  }
}

//定义数组用来保存当前面包屑导航显示的值
const breadcrumb  = ref([]);
//定义对象用来维护路由path与面包屑值的对应关系
let map = {
  '/user':['用户管理','用户列表'],
  '/vehicle':['车辆管理','车辆列表'],
  '/geofence':['围栏管理','电子围栏'],
  '/geofenceMap':['围栏管理','绘制围栏'],
  '/application':['调度管理','申请列表'],
  '/audit':['调度管理','调度审核'],
  '/distribute':['调度管理','用车分配'],
  '/dict':['字典管理','字典列表'],
  '/dictOption':['字典管理','字典项列表']
};
//useRoute()这个方法是Vue路由管理中的一个方法,可以向我们提供当前组件的路由对象
const route = useRoute();
//定义更新面包屑导航值的方法
const updateBreadcrumb = (route)=>{
  //获取当前路由的path
  const path = route.path;
  breadcrumb.value = map[path];
}
//一进入页面就立即调用的方法
onMounted(()=>{
  //这个只能保证每次渲染页面,也就是第一次进页面的时候会更新面包屑
  updateBreadcrumb(route);
})
//获取路由守卫,监听路由,只要path发生了改变(包括点击左侧菜单),就立即更新面包屑的值,再跳转
const router = useRouter();
router.beforeEach((to, from, next) => {
  updateBreadcrumb(to);
  next();
})


// 弹出抽屉
const visibleDrawer=ref(false);
// 控制图标是否显示完整
const isChatIconVisible = ref(false);

// 定义智能客服对话
const CustommerChat = () => {
  visibleDrawer.value=true;
  // 打开抽屉时如果没有消息，添加欢迎语
  if (messageList.value.length === 0) {
    addMessage('bot', '您好，我是智能客服助手，请问有什么可以帮您？');
  }
}

// 关闭对话
const handleClose = () => {
  visibleDrawer.value=false;
}

// 消息列表
const messageList = ref([]);
// 用户输入
const userMessage = ref('');
// 是否正在发送
const isSending = ref(false);
// 是否正在加载
const isLoading = ref(false);
// 消息列表容器引用
const messageListRef = ref(null);

// 添加消息
const addMessage = (role, content) => {
  const now = new Date();
  const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;
  messageList.value.push({
    role,
    content,
    time
  });
  // 滚动到底部
  nextTick(() => {
    scrollToBottom();
  });
};

// 滚动到底部
const scrollToBottom = () => {
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight;
  }
};

// 发送消息
const sendMessage = async () => {
  if (!userMessage.value.trim() || isSending.value) {
    return;
  }

  const message = userMessage.value.trim();
  userMessage.value = '';
  
  // 添加用户消息
  addMessage('user', message);
  isSending.value = true;
  isLoading.value = true;

  try {
    // 先添加机器人消息占位
    addMessage('bot', '');
    
    // 使用 EventSource 接收 SSE 流
    await fetchStream(message);
    
    isLoading.value = false;
  } catch (error) {
    console.error('发送消息失败:', error);
    ElMessage.error('消息发送失败，请稍后重试');
    // 移除失败的消息占位
    messageList.value.pop();
    addMessage('bot', '抱歉，暂时无法连接到智能客服，请稍后重试。');
    isLoading.value = false;
  } finally {
    isSending.value = false;
  }
};

// 处理流式响应
const fetchStream = async (message) => {
  return new Promise((resolve, reject) => {
    const url = `${BASE_URL}/ai/chat?userMessage=${encodeURIComponent(message)}`;
    const eventSource = new EventSource(url);
    let accumulatedContent = '';
    let hasReceivedData = false;

    eventSource.onmessage = (event) => {
      hasReceivedData = true;
      accumulatedContent += event.data;
      // 更新最后一条消息的内容
      const botMessageIndex = messageList.value.length - 1;
      if (messageList.value[botMessageIndex]) {
        messageList.value[botMessageIndex].content = accumulatedContent;
        scrollToBottom();
      }
    };

    eventSource.onerror = (error) => {
      console.error('SSE 错误:', error);
      eventSource.close();
      
      // 如果已经接收到数据，则认为成功
      if (hasReceivedData && accumulatedContent) {
        resolve(accumulatedContent);
      } else {
        reject(new Error('连接失败'));
      }
    };
  });
};






</script>

<style scoped>
/* 聊天图标按钮样式 */
.chat-icon-button:hover {
  right: 0 !important;
}

.chat-icon-show {
  right: 0 !important;
}

/* 聊天容器 */
.chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 80px);
  padding: 20px;
}

/* 消息列表 */
.message-list {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 20px;
  padding-right: 5px;
}

/* 消息项 */
.message-item {
  display: flex;
  margin-bottom: 20px;
  align-items: flex-start;
}

.message-user {
  flex-direction: row-reverse;
}

/* 消息头像 */
.message-avatar {
  flex-shrink: 0;
  margin: 0 10px;
}

/* 消息内容 */
.message-content {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}

.message-user .message-content {
  align-items: flex-end;
}

/* 消息气泡 */
.message-bubble {
  padding: 12px 16px;
  border-radius: 8px;
  background-color: #f0f0f0;
  color: #333;
  line-height: 1.5;
  word-wrap: break-word;
}

.message-user .message-bubble {
  background-color: #409EFF;
  color: #fff;
}

/* 消息时间 */
.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

/* 加载动画 */
.message-bubble.loading {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.dot {
  display: inline-block;
}

.dots {
  display: inline-block;
  animation: ellipsis 1.5s infinite;
}

@keyframes ellipsis {
  0%, 20% {
    content: '.';
  }
  40%, 60% {
    content: '..';
  }
  80%, 100% {
    content: '...';
  }
}

/* 输入区域 */
.input-area {
  border-top: 1px solid #e0e0e0;
  padding-top: 15px;
}

/* 滚动条样式 */
.message-list::-webkit-scrollbar {
  width: 6px;
}

.message-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.message-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.message-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>