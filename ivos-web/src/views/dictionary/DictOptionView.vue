<!--字典项页-->
<template>
  <div style="background-color:#fff;padding:10px 20px;">
    <span style="line-height:60px;font-size:20px;">字典项管理</span>
    <el-button @click="goBack" type="primary" style="float:right;margin-top:13px;">返回</el-button>
  </div>
  <!-- 字典项搜索卡片 -->
  <el-card style="margin:20px;height: 70px;">
    <el-form :inline="true">
      <el-form-item label="字典项名称">
        <el-input placeholder="请输入" style="width:220px;" v-model="searchForm.label"
                  @keydown.enter.prevent="selectDictOption"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="resetSearch">重置</el-button>
        <el-button type="primary" @click="selectDictOption">搜索</el-button>
      </el-form-item>
    </el-form>
  </el-card>
  <el-card style="margin:20px;">
    <div style="margin:10px 0px 40px 10px;">
      字典项列表
      <el-button type="primary" style="float: right;margin-top:-3px;" @click="beforeAdd">新增字典项</el-button>
    </div>
    <el-table :data="dictOptionArr">
      <el-table-column type="index" label="编号" width="80" align="center"/>
      <el-table-column prop="label" label="字典项名称" align="center"/>
      <el-table-column prop="value" label="字典项值" align="center"/>
      <el-table-column prop="sort" label="字典项排序" align="center"/>
      <el-table-column prop="remark" label="备注信息" align="center" width="320"/>
      <el-table-column prop="createTime" label="创建时间" align="center"/>
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button type="primary" link @click="editDictOption(scope.row.id)">编辑</el-button>
          <el-button type="primary" link @click="deleteDictOption(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
  <!-- 保存字典项弹窗 -->
  <el-dialog :title="dialogTitle" v-model="dialogVisible" style="width:1000px;padding:40px;"
             :before-close="handleClose">
    <el-form label-width="80px" label-position="top">
      <el-form-item label="字典项名称">
        <el-input placeholder="请输入" v-model="saveDictOptionForm.label"></el-input>
      </el-form-item>
      <el-form-item label="字典项值">
        <el-input placeholder="请输入" v-model="saveDictOptionForm.value"></el-input>
      </el-form-item>
      <el-form-item label="字典项排序">
        <el-input placeholder="请输入" v-model="saveDictOptionForm.sort"></el-input>
      </el-form-item>
      <el-form-item label="备注信息">
        <el-input placeholder="请输入" v-model="saveDictOptionForm.remark"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="saveDictOption">保存</el-button>
    </template>
  </el-dialog>
</template>
<script setup>
import router from "@/router";
import {onMounted, ref} from "vue";
import qs from "qs";
import axios from "axios";
import {ElMessage} from "element-plus";

//定义返回字典页的方法
const goBack = () => {
  router.push('/dict');
}
//获取URL中携带过来的字典id
//http://localhost:9090/dictOption?id=105 中的 105
const dictId = new URLSearchParams(window.location.search).get('id');
//定义对象用来保存字典项的搜索条件
const searchForm = ref({label:'',dictId:dictId});
//定义数组用来保存字典项表格数据
const dictOptionArr = ref([]);
//定义搜索字典项的方法
const selectDictOption = () => {
  let data = qs.stringify(searchForm.value);
  axios.get(BASE_URL+'/v1/dictoption/select?'+data).then((response)=>{
    if (response.data.code === 2000){
      dictOptionArr.value = response.data.data;
    }else {
      ElMessage.error(response.data.message);
    }
  })
}
onMounted(()=>{
  selectDictOption();
})
//定义重置搜索的方法
const resetSearch = () => {
  searchForm.value = {label:'',dictId:dictId};//清空搜索条件
  selectDictOption();//重新加载所有字典项
}
//定义变量用来控制弹窗是否出现
const dialogVisible = ref(false);
//定义变量用来保存弹窗标题
const dialogTitle = ref('新增字典项');
//定义对象用来保存弹窗表单数据
const saveDictOptionForm = ref({
  label:'',
  value:'',
  sort:'',
  remark:'',
  dictId:dictId
});
//定义取消弹窗的方法
const handleClose = () => {
  if(confirm('您确认要取消保存吗?')){
    saveDictOptionForm.value = {label:'',value:'',sort:'',remark:'',dictId:dictId};//清空弹窗表单数据
    dialogVisible.value = false;//关闭弹窗
  }
}
//定义保存字典项的方法
const saveDictOption = () => {
  let data = qs.stringify(saveDictOptionForm.value);
  axios.post(BASE_URL+'/v1/dictoption/save',data).then((response)=>{
    if (response.data.code === 2000){
      ElMessage.success('保存成功!');
      saveDictOptionForm.value = {label:'',value:'',sort:'',remark:'',dictId:dictId};//清空弹窗表单数据
      dialogVisible.value = false;//关闭弹窗
      selectDictOption();//重新加载所有字典项
    }else if(response.data.code == 3001){
      ElMessage.error(response.data.data);
    }else {
      ElMessage.error(response.data.message);
    }
  })
}
//定义点击"新增字典项"按钮时调用的方法
const beforeAdd = () => {
  dialogTitle.value = '新增字典项';
  dialogVisible.value = true;
}
//定义编辑字典项的方法
const editDictOption = (id) => {
  dialogTitle.value = '编辑字典项';
  dialogVisible.value = true;
  axios.get(BASE_URL+'/v1/dictoption/select?id='+id).then((response)=>{
    if (response.data.code === 2000){
      saveDictOptionForm.value = response.data.data[0];
    }else{
      ElMessage.error(response.data.message);
    }
  })
}
//定义删除字典项的方法
const deleteDictOption = (id) => {
  if(confirm('您确认要删除该字典项吗?')){
    axios.post(BASE_URL+'/v1/dictoption/delete/'+id).then((response)=>{
      if(response.data.code === 2000){
        ElMessage.success('删除成功!');
        selectDictOption();//重新加载所有字典项
      }else{
        ElMessage.error(response.data.message);
      }
    })
  }
}
</script>

<style scoped>

</style>