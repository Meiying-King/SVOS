<!--电子围栏页 GeofenceView.vue-->
<template>
  <div style="height: 6vh;background-color:#fff;padding:10px 20px;">
    <span style="line-height:60px;font-size:20px;">围栏管理</span>
    <el-button type="primary" style="float:right;margin-top:13px;"
               @click="router.push('/geofenceMap')">新建围栏</el-button>
  </div>
  <!-- 围栏查询卡片 -->
  <el-card style="margin:20px;height: 70px;">
    <el-form :inline="true">
      <el-form-item label="围栏名称">
        <el-input placeholder="请输入" style="width:220px;"
                  v-model="geoSearchForm.name" @keydown.enter="loadGeo"></el-input>
      </el-form-item>
      <el-form-item label="围栏状态" style="width:290px;">
        <el-select placeholder="请选择"
                   v-model="geoSearchForm.status" @change="loadGeo">
          <el-option label="启用" value="1"/>
          <el-option label="禁用" value="0"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="resetSearch">重置</el-button>
        <el-button type="primary" @click="loadGeo">查询</el-button>
      </el-form-item>
    </el-form>
  </el-card>
  <!-- 围栏表格数据 -->
  <el-card style="margin:20px;">
    <el-table stripe :data="geoTableData">
      <el-table-column label="编号" prop="id" type="index" align="center" width="100"></el-table-column>
      <el-table-column label="围栏名称" prop="name" align="center"></el-table-column>
      <el-table-column label="围栏坐标点" prop="position" align="center" show-overflow-tooltip="true" width="500"></el-table-column>
      <el-table-column label="创建时间" prop="createTime" align="center"></el-table-column>
      <el-table-column label="围栏状态" align="center">
        <template #default="scope">
          <el-switch inactive-value="0" active-value="1"
                     v-model="scope.row.status"
                     @change="changeStatus(scope.row.id,scope.row.status)"></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button type="primary" link
                     @click="loadVehicle(scope.row.id)">管理车辆</el-button>
          <el-button type="primary" link
                     @click="deleteGeo(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
  <!-- 已绑定车辆弹窗 -->
  <el-dialog title="已绑定车辆" v-model="bindVehicleDialogVisible" style="padding:40px;">
    <el-button type="primary" style="margin:10px 0;" @click="showUnbindVehicle">添加绑定车辆</el-button>
    <el-table :data="bindVehicleArr">
      <el-table-column prop="brand" label="车辆品牌" align="center"></el-table-column>
      <el-table-column prop="license" label="车牌号" align="center"></el-table-column>
      <el-table-column prop="type" label="车辆类型" align="center"></el-table-column>
      <el-table-column
          label="操作"
          fixed="right"
          width="200"
          align="center">
        <template #default="scope">
          <el-button link type="primary" @click="removeBindVehicle(scope.row.id)">移除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
  <!-- 添加可绑定车辆列表弹窗 -->
  <el-dialog title="可绑定车辆列表" v-model="AddBindVehicleDialogVisible"
             :before-close="handleClose">
    <el-table :data="unbindVehicleArr">
      <el-table-column label="编号" type="index" align="center" width="80px"></el-table-column>
      <el-table-column prop="brand" label="车辆品牌" align="center"></el-table-column>
      <el-table-column prop="license" label="车牌号" align="center"></el-table-column>
      <el-table-column prop="type" label="车辆类型" align="center"></el-table-column>
      <el-table-column prop="status" label="车辆状态" align="center"></el-table-column>
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button link type="primary" @click="bindNewVhicle(scope.row.id)">绑定</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>
<script setup>
import {onMounted, ref} from "vue";
import axios from "axios";
import qs from "qs";
import {ElMessage} from "element-plus";
import router from "@/router";

//控制"已绑定车辆弹窗"是否显示
const bindVehicleDialogVisible = ref(false);
//控制"可绑定车辆列表"是否显示
const AddBindVehicleDialogVisible = ref(false);

//定义对象保存查询条件
const geoSearchForm = ref({name: '', status: ''})
//定义对象保存围栏表格数据
const geoTableData = ref([]);
//加载电子围栏数据
const loadGeo = () => {
  //console.log(geoSearchForm.value);
  let data = qs.stringify(geoSearchForm.value);
  axios.get(BASE_URL+'/v1/geofence/select?'+data).then((response)=>{
    if (response.data.code === 2000){
      geoTableData.value = response.data.data;
    }else {
      ElMessage.error(response.data.message);
    }
  })
}
onMounted(()=>{
  loadGeo();
})
//查询重置按钮
const resetSearch = () => {
  geoSearchForm.value = {};
  loadGeo();
}
//定义用来保存绑定在指定围栏上所有车辆的数组,也就是外层弹窗表格数据
const bindVehicleArr = ref([]);
/* 定义全局变量用来保存当前要操作的围栏id */
var geoId;
//定义点击"管理车辆"按钮时调用的方法
const loadVehicle = (geofenceId) => {
  geoId = geofenceId;
  bindVehicleDialogVisible.value = true;
  axios.get(BASE_URL+'/v1/vehicle/select?geofenceId='+geofenceId)
      .then((response)=>{
        if (response.data.code === 2000){
          bindVehicleArr.value = response.data.data;
        }else {
          ElMessage.error(response.data.message);
        }
  })
}
//点击移除按钮时调用的方法:移除当前围栏上绑定的当前车辆
const removeBindVehicle = (vehicleId)=>{
  axios.post(BASE_URL+'/v1/vehicle/unbind/'+vehicleId).then((response)=>{
    if (response.data.code === 2000){
      ElMessage.success('移除成功!');
      /* 重新加载当前围栏上绑定的车辆 */
      loadVehicle(geoId);
    }else {
      ElMessage.error(response.data.message);
    }
  })
}
//定义数组用来保存未绑定围栏的车辆列表
const unbindVehicleArr = ref([]);
//定义点击外层弹窗"添加绑定车辆"按钮时调用的方法
const showUnbindVehicle = () => {
  AddBindVehicleDialogVisible.value = true;
  axios.get(BASE_URL+'/v1/vehicle/select?geofenceBindStatus=0')
      .then((response)=>{
    if (response.data.code === 2000){
      unbindVehicleArr.value = response.data.data;
    }else {
      ElMessage.error(response.data.message);
    }
  })
}
//定义给围栏绑定指定车辆的方法
const bindNewVhicle = (id)=>{
  axios.post(BASE_URL+'/v1/vehicle/bind/'+geoId+'/'+id)
      .then((response)=>{
        if (response.data.code === 2000){
          ElMessage.success('绑定成功!');
          //每成功绑定走一辆车,内层弹窗就要重新加载所有未绑定围栏的车辆列表
          showUnbindVehicle();
        }else {
          ElMessage.error(response.data.message);
        }
      })
}
//在关闭内层弹窗之前,要重新刷新外层弹窗的数据
const handleClose = ()=>{
  //重新加载绑定在当前围栏上的所有的车
  loadVehicle(geoId);
  //再关闭内层弹窗
  AddBindVehicleDialogVisible.value = false;
}

//定义修改围栏状态的方法
const changeStatus = (id,status)=>{
  axios.post(BASE_URL+'/v1/geofence/update/'+id+'/'+status)
      .then((response)=>{
        if (response.data.code === 2000){
          ElMessage.success('状态修改成功!');
        }else {
          ElMessage.error(response.data.message);
        }
      })
}
//定义删除围栏的方法
const deleteGeo = (id)=>{
  if(confirm('您确认要删除此围栏吗?')){
    axios.post(BASE_URL+'/v1/geofence/delete/'+id).then((response)=>{
      if(response.data.code === 2000){
        ElMessage.success('删除成功!');
        //删除成功后重新加载数据
        loadGeo();
      }else{
        ElMessage.error(response.data.message);
      }
    })

  }
}




</script>

<style scoped>
</style>