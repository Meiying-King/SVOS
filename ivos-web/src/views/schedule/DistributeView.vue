<!--车辆分配管理页-->
<template>
  <div style="height: 6vh;background-color:#fff;padding:10px 20px;">
    <span style="line-height:60px;font-size:20px;">车辆分配</span>
  </div>
  <!-- 申请单搜索卡片 -->
  <el-card style="margin:20px;height: 70px;">
    <el-form :inline="true">
      <el-form-item label="工单申请人">
        <el-input placeholder="请输入申请人" style="width:220px;"
                  v-model="search.username" @keydown.enter.prevent="loadApplication"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="resetSearch">重置</el-button>
        <el-button type="primary" @click="loadApplication">查询</el-button>
      </el-form-item>
    </el-form>
  </el-card>

  <!-- 工单信息列表 -->
  <el-card  style="margin:20px;">
    <el-radio-group style="margin-bottom: 15px;" v-model="type">
      <el-radio-button value="50" size="large" @change="loadApplication">待分配</el-radio-button>
      <el-radio-button value="60" size="large" @change="loadApplication">已分配</el-radio-button>
    </el-radio-group>
    <el-table :data="appTableData">
      <el-table-column label="编号" type="index" width="100" align="center"></el-table-column>
      <el-table-column label="工单申请人" prop="username" align="center"></el-table-column>
      <el-table-column label="申请时间" prop="createTime" align="center"></el-table-column>
      <el-table-column label="出发地" prop="departureAddr" align="center"></el-table-column>
      <el-table-column label="目的地" prop="destinationAddr" align="center"></el-table-column>
      <el-table-column label="用车事由" prop="reason" align="center"></el-table-column>
      <el-table-column label="使用开始时间" prop="startTime" align="center"></el-table-column>
      <el-table-column label="使用结束时间" prop="endTime" align="center"></el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template #default="scope">
          <el-button type="primary" link v-if="type==50" @click="loadGeo(scope.row.id)">分配用车</el-button>
          <el-button type="primary" link v-if="type==60" @click="back(scope.row)">还车</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <!-- 围栏车辆概览弹窗 -->
  <el-dialog title="围栏列表" v-model="dialogVisible" style="padding:40px;">
    <el-form>
      <el-row :gutter="10">
        <el-col :span="8">
          <el-form-item label="围栏名称:">
            <el-input placeholder="请输入"
                      v-model="searchGeofenceForm.name"
                      @keydown.enter="loadGeo"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="围栏状态">
            <el-select placeholder="请选择"
                       v-model="searchGeofenceForm.status"
                       @change="loadGeo">
              <el-option label="启用" value="1"/>
              <el-option label="禁用" value="0"/>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="4"></el-col>
        <el-col :span="4">
          <el-form-item>
            <el-button-group>
              <el-button @click="resetSearchGeo">重置</el-button>
              <el-button type="primary" @click="loadGeo">查询</el-button>
            </el-button-group>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-table style="margin:20px 0" :data="geoTableData">
      <el-table-column label="编号" type="index" width="80" align="center"></el-table-column>
      <el-table-column label="围栏名称" prop="name" width="200" align="center"></el-table-column>
      <el-table-column label="车辆总数" prop="totalNum" width="200" align="center"></el-table-column>
      <el-table-column label="可用车辆" prop="availableNum" align="center"></el-table-column>
      <el-table-column label="围栏状态" prop="status" align="center"></el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template #default="scope">
          <el-button type="primary" link
                     @click="showVehicle(scope.row.id)">查看车辆</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>

  <!-- 分配车辆弹窗 -->
  <el-dialog title="分配车辆" style="padding:40px;" v-model="vehicleDialogVisible">
    <el-table style="margin:20px 0" :data="vehicleTableData">
      <el-table-column label="编号" type="index" width="80" align="center"></el-table-column>
      <el-table-column label="车辆品牌" prop="brand" width="200" align="center"></el-table-column>
      <el-table-column label="车牌号" prop="license" width="200" align="center"></el-table-column>
      <el-table-column label="车辆类型" prop="type" align="center"></el-table-column>
      <el-table-column label="车辆状态" prop="status" align="center"></el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template #default="scope">
          <el-button type="primary" link :disabled="scope.row.status=='2'"
                     @click="distribute(scope.row.id)">分配</el-button>
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
//用于控制围栏车辆概览弹窗是否可见
const dialogVisible = ref(false);
//用于控制分配车辆弹窗是否可见
const vehicleDialogVisible = ref(false);

//定义变量用来保存申请单的查询条件
const search = ref({username:''});
//定义变量用来保存申请单状态单选组的值
const type = ref('50');
//定义数组用来保存申请单表格数据
const appTableData = ref([]);
//定义加载申请单的方法
const loadApplication = ()=>{
  search.value.status = type.value;
  let data = qs.stringify(search.value);
  axios.get(BASE_URL+'/v1/application/select?'+data).then((response)=>{
    if(response.data.code == 2000){
      appTableData.value = response.data.data;
    }else{
      ElMessage.error(response.data.message);
    }
  })
}
//定义重置搜索的方法
const resetSearch = ()=>{
  search.value.username = '';
  loadApplication();
}
onMounted(()=>{
  loadApplication();
})

//定义对象用来保存围栏搜索条件
const searchGeofenceForm = ref({
  name: '',
  status: ''
});
//定义数组用来保存围栏表格数据
const geoTableData = ref([]);
//定义全局变量用来保存申请单id
var applicationId;
//定义加载围栏的方法
const loadGeo = (id)=>{
  //将当前行申请单的编号赋值给全局变量
  applicationId = id;
  //控制弹窗出现
  dialogVisible.value = true;
  let data = qs.stringify(searchGeofenceForm.value);
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
//定义重置搜索的方法
const resetSearchGeo = ()=>{
  searchGeofenceForm.value = {};
  loadGeo();
}
//定义数组用来保存围栏车辆数据
const vehicleTableData = ref([]);
//定义显示指定围栏上可用车辆列表的方法
const showVehicle = (id)=>{
  vehicleDialogVisible.value = true;
  axios.get(BASE_URL+'/v1/vehicle/select?geofenceId='+id).then((response)=>{
    if (response.data.code === 2000){
      vehicleTableData.value = response.data.data;
    }else {
      ElMessage.error(response.data.message);
    }
  })
}
//定义分配车辆的方法
const distribute = (vehicleId)=>{
  axios.post(BASE_URL+'/v1/application/distribute/'+applicationId+'/'+vehicleId)
      .then((response)=>{
        if (response.data.code === 2000){
          ElMessage.success('分配成功!');
          vehicleDialogVisible.value = false;
          dialogVisible.value = false;
          loadApplication();/* 刷新当前申请单的列表 */
        }else {
          ElMessage.error(response.data.message);
        }
      })
}
//定义还车方法 修改申请(vehicleId:null)  修改车辆
const back = (row)=>{
  axios.post(BASE_URL+'/v1/application/back/'+row.id+'/'+row.vehicleId)
  .then((response)=>{
    if (response.data.code === 2000){
      ElMessage.success('还车成功!');
      loadApplication();/* 刷新当前申请单的列表 */
    }else {
      ElMessage.error(response.data.message);
    }
  })
}


</script>

<style scoped>

</style>