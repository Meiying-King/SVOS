<!--字典管理页-->
<template>
  <div style="background-color:#fff;padding:10px 20px;">
    <span style="line-height:60px;font-size:20px;">字典管理</span>
  </div>
  <!-- 字典数据搜索卡片 -->
  <el-card style="margin:20px;height: 70px;">
    <el-form :inline="true">
      <el-form-item label="字典名称">
        <el-input placeholder="请输入" style="width:220px;" v-model="searchForm.name"
                  @keydown.enter="selectDict"></el-input>
      </el-form-item>
      <el-form-item label="字典编码">
        <el-input placeholder="请输入" style="width:220px;" v-model="searchForm.code"
                  @keydown.enter="selectDict"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="resetSearch">重置</el-button>
        <el-button type="primary" @click="selectDict">搜索</el-button>
      </el-form-item>
    </el-form>
  </el-card>
  <el-card style="margin:20px;">
    <div style="margin:10px 0px 40px 10px;">
      字典列表
      <el-button type="primary" style="float: right;margin-top:-3px;" @click="addDict">新增字典</el-button>
    </div>
    <el-table :data="dictArr">
      <el-table-column type="index" label="编号" width="80" align="center"/>
      <el-table-column prop="name" label="字典名称" align="center"/>
      <el-table-column prop="code" label="字典编码" align="center">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="showDictOption(scope.row.id)">{{ scope.row.code }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注信息" align="center"/>
      <el-table-column prop="createTime" label="创建时间" align="center"/>
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="editDict(scope.row.id)">编辑</el-button>
          <el-button link type="primary" size="small" @click="deleteDict(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <!-- 保存字典弹窗 -->
  <el-dialog :title="dialogTitle" v-model="dialogVisible" style="width:1000px;padding:40px;"
             :before-close="handleClose">
    <el-form label-width="80px" label-position="top">
      <el-form-item label="字典名称">
        <el-input placeholder="请输入" v-model="saveDictForm.name"></el-input>
      </el-form-item>
      <el-form-item label="字典编码">
        <el-input placeholder="请输入" v-model="saveDictForm.code"></el-input>
      </el-form-item>
      <el-form-item label="备注信息">
        <el-input placeholder="请输入" v-model="saveDictForm.remark"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="saveDict">保存</el-button>
    </template>
  </el-dialog>
</template>
<script setup>
import {onMounted, ref} from "vue";
import axios from "axios";
import qs from "qs";
import {ElMessage} from "element-plus";
import router from "@/router";

//定义对象用来保存字典的搜索条件
const searchForm = ref({name:'',code:''});
//定义数组用来保存查出来的字典表格数据
const dictArr = ref([]);
//定义搜索字典的方法
const selectDict = () => {
  let data = qs.stringify(searchForm.value);
  axios.get(BASE_URL+'/v1/dict/select?'+data).then((response)=>{
    if (response.data.code === 2000){
      dictArr.value = response.data.data;
    }else {
      ElMessage.error(response.data.message);
    }
  })
}
onMounted(()=>{
  selectDict();
})
//定义重置搜索的方法
const resetSearch = () => {
  searchForm.value = {};//清空搜索条件
  selectDict();//重新加载所有字典
}
//定义显示指定字典对应的多个字典项的方法
//参数为当前字典id,因为我们需要查此字典对应的多个字典项,而不是所有的字典项
const showDictOption = (id) => {
  router.push('/dictOption?id='+id);
}

//定义点击新增字典按钮时调用的方法
const addDict = () => {
  dialogVisible.value = true;
  dialogTitle.value = '新增字典';
}
//定义对象用来保存弹窗表单数据
const saveDictForm = ref({name:'',code:'',remark:''});
//定义保存字典的方法
const saveDict = () => {
  let data = qs.stringify(saveDictForm.value);
  axios.post(BASE_URL+'/v1/dict/save',data).then((response)=>{
    if (response.data.code === 2000){
      ElMessage.success('保存成功!');
      saveDictForm.value = {};//清空弹窗表单数据
      dialogVisible.value = false;//关闭弹窗
      selectDict();//重新加载字典列表
    }else if(response.data.code == 3001){
      ElMessage.error(response.data.data);
    }else {
      ElMessage.error(response.data.message);
    }
  })
}
//定义处理弹窗关闭的方法
const handleClose = () => {
  if(confirm('您确认要取消保存吗?')){
    saveDictForm.value = {};
    dialogVisible.value = false;
  }
}

//定义编辑字典的方法
const editDict = (id) => {
  dialogVisible.value = true;
  dialogTitle.value = '编辑字典';
  axios.get(BASE_URL+'/v1/dict/select?id='+id).then((response)=>{
    if (response.data.code === 2000){
      saveDictForm.value = response.data.data[0];
    }else{
      ElMessage.error(response.data.message);
    }
  })
}
//定义变量用来控制弹窗是否出现
const dialogVisible = ref(false);
//定义变量用来保存弹窗标题
const dialogTitle = ref('新增字典');

//定义删除字典的方法
const deleteDict = (id) => {
  if(confirm('您确认要删除该字典吗?')){
    axios.post(BASE_URL+'/v1/dict/delete/'+id).then((response)=>{
      if(response.data.code === 2000){
        ElMessage.success('删除成功!');
        selectDict();//重新加载所有字典
      }else{
        ElMessage.error(response.data.message);
      }
    })
  }
}

</script>

<style scoped>

</style>