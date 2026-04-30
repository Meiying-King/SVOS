<!-- 用户管理页 -->
<template>
  <div style="height:6vh;background-color:#fff;padding: 10px 20px;">
    <span style="font-size: 20px;line-height: 60px;">用户管理</span>
    <el-button type="primary" @click="beforeAddUser" style="float:right;margin-top: 10px;">
      新建用户
    </el-button>
  </div>
  <!-- 用户搜索卡片 -->
  <el-card style="height: 70px;margin: 10px;">
    <el-form :inline="true">
      <el-form-item label="用户搜索" style="width: 270px;">
        <el-input placeholder="请输入用户名"
                  v-model="searchUserForm.username" @keydown.enter="loadUser"></el-input>
      </el-form-item>
      <el-form-item label="用户状态" style="width: 270px;">
        <el-select placeholder="请选择用户状态"
                   v-model="searchUserForm.status" @change="loadUser">
          <el-option label="启用" value="1"></el-option>
          <el-option label="禁用" value="0"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="resetSearch">重置</el-button>
        <el-button type="primary" @click="loadUser">查询</el-button>
      </el-form-item>
    </el-form>
  </el-card>
  <!-- 用户表格卡片 -->
  <el-card style="margin: 10px;">
    <el-table :data="userArr">
      <el-table-column label="编号" type="index" align="center" width="80"></el-table-column>
      <el-table-column label="用户名" prop="username" align="center"></el-table-column>
      <el-table-column label="手机号" prop="phone" align="center"></el-table-column>
      <el-table-column label="加入时间" prop="createTime" align="center"></el-table-column>
      <el-table-column label="用户状态" align="center">
        <template #default="scope">
          <el-switch v-model="scope.row.status"
                     active-value="1" inactive-value="0"
                     @change="changeStatus(scope.row.id,scope.row.status)"></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button type="primary" link
                     :disabled="scope.row.level==40"
                     @click="resetPassword(scope.row.id)">重置密码</el-button>
          <el-button type="primary" link
                     :disabled="scope.row.level==40"
                     @click="editUser(scope.row.id)">编辑</el-button>
          <el-button type="primary" link
                     :disabled="scope.row.level==40"
                     @click="deleteUser(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <!-- 用户新建弹窗 -->
  <el-dialog
      :title="dialogTitle"
      style="width: 1000px;padding: 40px;"
      v-model="dialogVisible"
      :before-close="handleClose"
  >
    <el-form label-position="top">
      <el-row :gutter="10">
        <el-col :span="12">
          <el-form-item label="用户名">
            <el-input placeholder="请输入用户名"
                      v-model="saveUserForm.username"
                      :disabled="saveUserForm.id!=null"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="手机号">
            <el-input placeholder="请输入手机号" v-model="saveUserForm.phone"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="10">
        <el-col :span="12">
          <el-form-item label="邮箱">
            <el-input placeholder="请输入邮箱" v-model="saveUserForm.email"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="年龄">
            <el-input placeholder="请输入年龄" v-model="saveUserForm.age"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="10">
        <el-col :span="6">
          <el-form-item label="职级">
            <el-select placeholder="请选择" v-model="saveUserForm.level"
                       @change="loadLeader">
              <el-option v-for="item in levelOptions"
                         :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="直属领导">
            <el-select placeholder="请选择" v-model="saveUserForm.parentId">
              <el-option v-for="item in leaderOptions"
                         :label="item.username" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="4">
          <el-form-item label="性别">
            <el-radio-group v-model="saveUserForm.gender">
              <el-radio border label="男" value="1" style="margin: 0;"></el-radio>
              <el-radio border label="女" value="0"></el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="状态">
            <el-radio-group v-model="saveUserForm.status">
              <el-radio border label="启用" value="1" style="margin: 0;"></el-radio>
              <el-radio border label="禁用" value="0"></el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="saveUser">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import {onMounted, ref} from "vue";
import axios from "axios";
import qs from "qs";
import {ElMessage} from "element-plus";

//定义对象用来保存用户的查询条件
const searchUserForm = ref({username:'',status:''});
//定义数组用来保存用户数据
const userArr = ref([]);
//定义加载用户表格数据的方法
const loadUser = ()=>{
  //console.log(searchUserForm.value);
  //axios.get(BASE_URL+'/v1/user/select?username='+searchUserForm.value.username+'&status='+searchUserForm.value.status)
  //qs(query string) 可以帮我们直接将请求参数转为查询字符串格式 username=xxx&status=xxx
  let data = qs.stringify(searchUserForm.value);
  //console.log(data);
  axios.get(BASE_URL+'/v1/user/select?'+data).then((response)=>{
    if(response.data.code == 2000){
      userArr.value = response.data.data;
    }else{
      ElMessage.error(response.data.message);
    }
  })
}
//一进入页面也要立即调用加载用户的方法
onMounted(()=>{
  loadUser();
})
//定义重置搜索的方法
const resetSearch = ()=>{
  //清空所有查询条件,并重新加载所有用户数据
  searchUserForm.value = {username:'',status:''};
  loadUser();
}

//定义数组维护员工的职级选项
const levelOptions = ref([
  {label:'员工',value:'10'},
  {label:'经理',value:'20'},
  {label:'总监',value:'30'},
  {label:'总裁',value:'40'}
]);
//定义数组用来维护员工的可选的直属领导选项
const leaderOptions = ref([
  // {username:'AA',id:'301'},
  // {username:'BB',id:'302'}
]);
//定义对象用来保存从弹窗表单中收集到的数据
const saveUserForm = ref({
  username:'',
  phone:'',
  email:'',
  age:'',
  level:'',
  parentId:'',
  gender:'',
  status:''
});
//定义保存对象的方法
const saveUser = ()=>{
  console.log(saveUserForm.value);
  let data = qs.stringify(saveUserForm.value);
  axios.post(BASE_URL+'/v1/user/save',data)
      .then((response)=>{
        if(response.data.code == 2000){
          ElMessage.success('保存成功!');
          saveUserForm.value = {};//清空弹窗表单数据
          dialogVisible.value = false;//关闭弹窗
          loadUser();//重新加载用户数据
        }else if(response.data.code == 3001){//如果校验不通过
          ElMessage.error(response.data.data);//显示具体校验失败原因
        }else{
          ElMessage.error(response.data.message);
        }
      })
}
//处理弹窗关闭的方法
const handleClose = ()=>{
  if(confirm('您确认要取消保存吗?')){
    saveUserForm.value = {};//先清空表单数据
    dialogVisible.value = false;//再关闭弹窗
  }
}
//定义根据职级加载其直属领导可选项的方法
const loadLeader = ()=>{
  //为了防止之前已选择的直属领导数据和改动后的数据不匹配,可以把之前无用的数据清除
  saveUserForm.value.parentId = '';//清除选择的具体领导
  leaderOptions.value = [];//清除之前加载的领导选项数组
  let level = parseInt(saveUserForm.value.level)+10;//获取当前的职级+10
  axios.get(BASE_URL+'/v1/user/select?level='+level)
      .then((response)=>{
        if(response.data.code == 2000){
          leaderOptions.value = response.data.data;
        }else{
          ElMessage.error(response.data.message);
        }
      })
}

//定义变量用来保存弹窗标题
const dialogTitle = ref('新增员工');
//定义变量用来控制弹窗是否出现
const dialogVisible = ref(false);
//定义点击"新建用户"按钮时调用的方法
const beforeAddUser = ()=>{
  dialogTitle.value = '新增员工';
  dialogVisible.value = true;
}
//定义编辑用户的方法
const editUser= (id)=>{
  dialogTitle.value = '编辑员工';
  dialogVisible.value = true;
  //给后端发请求,回显此被编辑的员工数据
  axios.get(BASE_URL+'/v1/user/select?id='+id)
      .then((response)=>{
        if(response.data.code == 2000){
          //将员工数据保存到saveUserForm对象中
          saveUserForm.value = response.data.data[0];
          let level = parseInt(saveUserForm.value.level)+10;//获取当前的职级+10
          axios.get(BASE_URL+'/v1/user/select?level='+level)
              .then((response)=>{
                if(response.data.code == 2000){
                  leaderOptions.value = response.data.data;
                }else{
                  ElMessage.error(response.data.message);
                }
              })
        }else{
          ElMessage.error(response.data.message);
        }
      })
}
//定义修改员工状态的方法
const changeStatus = (userId,status)=>{
  axios.post(BASE_URL+'/v1/user/update/status/'+userId+'/'+status)
      .then((response)=>{
        if(response.data.code == 2000){
          ElMessage.success('修改员工状态成功!');
        }else{
          ElMessage.error(response.data.message);
        }
      })
}
//定义删除员工的方法
const deleteUser = (userId)=>{
  if(confirm('您确认要删除此员工吗?')){
    axios.post(BASE_URL+'/v1/user/delete/'+userId)
        .then((response)=>{
          if(response.data.code == 2000){
            ElMessage.success('删除员工成功!');
            loadUser();//重新加载用户数据
          }else{
            ElMessage.error(response.data.message);
          }
        })
  }
}
//定义重置密码的方法
const resetPassword = (userId)=>{
  if(confirm('您确认要重置此员工的密码吗?')){
    axios.post(BASE_URL+'/v1/user/update/password/'+userId)
        .then((response)=>{
          if(response.data.code == 2000){
            ElMessage.success('重置密码成功!');
          }else{
            ElMessage.error(response.data.message);
          }
        })
  }
}




</script>

<style scoped>

</style>