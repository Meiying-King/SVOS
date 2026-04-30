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
        <!--  里面是可变区域(各个子组件) -->
        <router-view/>
      </el-main>
    </div>
  </div>
</template>

<script setup>
import {onMounted, ref} from "vue";
import {useRoute, useRouter} from "vue-router";

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
    //还需要把已经取出来的user对象清空
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






</script>