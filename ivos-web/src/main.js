import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
//1.引入element-plus组件库与其样式
import ElementPlus, {ElMessage} from 'element-plus'
import 'element-plus/dist/index.css'
//2.引入element-plus图标库,并起别名为ElementPlusIconsVue
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
//6.1修改elementPlus默认中文
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import axios from "axios";

//9.1 配置后端服务器的地址
const BASE_URL = 'http://localhost:8080';
//window对象是浏览器内置的全局对象,给它设置的属性或方法就成为了全局属性或方法
//此全局属性可以在任意的script标签中使用
window.BASE_URL = BASE_URL;

const app = createApp(App)/* 创建vue实例,并起名为app */
//9.2 给app(Vue对象)也设置一个后端服务器的全局属性,方便在任意的template标签中使用
app.config.globalProperties.BASE_URL = BASE_URL;

//3.将所有导入的图标组件变为键值对数组,并依次遍历取出每一个图标组件[key, component]
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    // 4.将当前遍历到的图标组件注册为当前vue实例的全局组件
    app.component(key, component)
}
//5.为前vue实例应用element-plus组件库
//6.2 app.use(ElementPlus,{ locale: zhCn })将El本地化为中文
app.use(ElementPlus,{ locale: zhCn }).use(store).use(router).mount('#app')

//7.解决ResizeObserver Error
const debounce = (fn, delay) => {
    let timer = null;
    return function () {
        let context = this;
        let args = arguments;
        clearTimeout(timer);
        timer = setTimeout(function () {
            fn.apply(context, args);
        }, delay);
    }
}
const _ResizeObserver = window.ResizeObserver;
window.ResizeObserver = class ResizeObserver extends _ResizeObserver {
    constructor(callback) {
        callback = debounce(callback, 16);
        super(callback);
    }
}
//8.配置路由守卫
//to 即将要去的目标路由对象
//from 即将要离开的当前路由对象
//next 放行函数,必须调用next()方法,才能跳转到目标路由
router.beforeEach((to, from, next) => {
    //获取当前的用户数据
    let user = localStorage.user;
    //如果当前要进入的目标不是登录页,并且当前用户没有登录,则强制跳转到登录页
    if(to.path !== '/login' && !user){
        next({path:'/login'},ElMessage.error('请先登录!'));
    }else{
        next();//如果当前要进入的路由是登录页,或已登录,直接放行
    }
})
//10.获取当前登录人数据的方法
//登录成功后,会立即将当前用户的数据以字符串键值对的方式存入localStorage中
//设置一个全局方法,如果获取到了登录人数据,注意:此数据是JSON字符串,需要恢复成JS对象
//如果没有拿到登录人数据,则返回null
window.getUser = ()=>{
    return localStorage.user ? JSON.parse(localStorage.user) : null;
}

//11.调用后端根据字典code查对应所有字典项的方法
window.loadDictOptions = (object,dictCode)=>{
    axios.get(BASE_URL+'/v1/dictoption/select/'+dictCode)
        .then((response)=>{
        if(response.data.code == 2000){
            object.value = response.data.data;
        }else{
            ElMessage.error(response.data.message);
        }
    })
}




