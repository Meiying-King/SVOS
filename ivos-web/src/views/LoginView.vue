<!-- 登录页 -->
<template>
  <el-container style="height:100vh;background-image:linear-gradient(135deg,#1352F2, #3C82F5);overflow: hidden;">
    <el-header style="padding: 30px;">
      <el-row :gutter="10">
<!--        <el-col :span="3">-->
<!--          <img src="/imgs/login/logo.png" style="height: 50px;">-->
<!--        </el-col>-->
        <el-col :span="1">
          <img src="/imgs/login/auto.png" style="height: 50px;">
        </el-col>
        <el-col :span="1">
          <img src="/imgs/login/verticalLine.png" style="height: 50px;">
        </el-col>
        <el-col :span="19">
          <span style="font-size:30px;color: #fff;font-weight: bold;">智慧车辆运营管理系统</span>
        </el-col>
      </el-row>
    </el-header>
    <el-main>
      <el-row :gutter="40">
        <el-col :span="16" :xs="24" :sm="24" :md="16" :lg="16" :xl="16">
          <img src="/imgs/login/bg.png" style="width: 100%;margin-top: 60px;">
        </el-col>
        <el-col :span="8" :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
          <el-card style="margin-top: 80px;">
            <h3 style="text-align: center;">用户登录</h3>
            <el-form label-position="top">
              <el-form-item label="用户名">
                <el-input placeholder="请输入用户名" v-model="user.username"></el-input>
              </el-form-item>
              <el-form-item label="密码">
                <el-input placeholder="请输入密码" show-password v-model="user.password"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" style="width: 100%;" @click="login">登录</el-button>
              </el-form-item>
            </el-form>
            <div style="font-size: 14px;color: #666;text-align: center;padding: 15px 0;">
              (体验账号:tom 密码:123456)
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script setup>
import {ref} from "vue";
import axios from "axios";
import {ElMessage} from "element-plus";
import router from "@/router";

//定义变量用来保存用户的用户名和密码
const user = ref({username:'',password:''});
//定义点击登录按钮时调用的方法
const login = ()=>{
  console.log(user.value);
  //1.向服务器发请求需要借助 axios
  axios.post(BASE_URL+'/v1/user/login',user.value)
      .then((response)=>{
        //回调函数:服务器响应后对结果进行进一步处理的函数,与发请求是异步的
        //response:服务器的响应对象,我们可以从response.data中拿到服务器响应的数据
        console.log(response.data);
        if(response.data.code == 2000){
          ElMessage.success('登录成功!');
          //console.log("**********************星期五")
          //console.log(response.data.data);//这个是JsonResult的data属性值,也就是userVO
          //console.log(JSON.stringify(response.data.data));//将userVO转为JSON格式的字符串
          /** 登录成功后,可以将后端返回的用户数据存入localStorage中
           * localStorage存的是字符串类型的键值对 */
          localStorage.setItem('user',JSON.stringify(response.data.data));
          router.push('/');//跳转到首页再重定向到用户管理页
        }else if(response.data.code == 3001){//校验失败
          ElMessage.error(response.data.data);//显示具体的校验失败原因
        }else{
          ElMessage.error(response.data.message);//其它错误显示具体的异常描述信息
          //登录失败清空输入框
          user.value = {username: '', password: ''};
        }
      })
}
</script>

<style scoped>

</style>