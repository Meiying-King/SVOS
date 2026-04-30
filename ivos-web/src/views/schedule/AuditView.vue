<!--申请审批页-->
<template>
  <!-- 顶部条 -->
  <div style="height: 6vh;background-color:#fff;padding:10px 20px;">
    <span style="line-height:60px;font-size:20px;">审批列表</span>
  </div>
  <!-- 审批搜索卡片 -->
  <el-card style="margin:20px;height: 70px;">
    <el-form :inline="true">
      <el-form-item label="用车人">
        <el-input placeholder="请输入用车人" style="width:220px;"
                  v-model="search.username"
                  @keydown.enter.prevent="loadAudit"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="resetSearch">重置</el-button>
        <el-button type="primary" @click="loadAudit">查询</el-button>
      </el-form-item>
    </el-form>
  </el-card>

  <!-- 审批单主体 -->
  <el-card style="margin:20px;">
    <!--  审批状态项  -->
    <el-radio-group style="margin-bottom: 15px;" v-model="type" @change="loadAudit">
      <el-radio-button value="10" size="large">待我审核</el-radio-button>
      <el-radio-button value="20" size="large">待他人审核</el-radio-button>
      <el-radio-button value="30" size="large">已审核</el-radio-button>
      <el-radio-button value="40" size="large">驳回</el-radio-button>
    </el-radio-group>
    <!--  审批列表  -->
    <el-table :data="auditArr">
      <el-table-column label="编号" prop="id" align="center" width="55" type="index"></el-table-column>
      <el-table-column label="用车人" prop="username" align="center" width="110"></el-table-column>
      <el-table-column label="开始时间" prop="startTime"  align="center"></el-table-column>
      <el-table-column label="结束时间" prop="endTime"  align="center"></el-table-column>
      <el-table-column label="用车事由" prop="reason"  align="center"></el-table-column>
      <el-table-column label="审批人" prop="auditUsernameList"  align="center"></el-table-column>
      <el-table-column label="出发地" prop="departureAddr"  align="center"></el-table-column>
      <el-table-column label="目的地" prop="destinationAddr"  align="center"></el-table-column>
      <el-table-column label="操作" width="100" align="center" v-if="type==10||type==40" :key="audit">
        <template #default="scope">
          <!-- 审批10：在待我审核页签下显示，需要当前登录用户进行审批 -->
          <el-button type="primary" link v-if="type==10"
                     @click="auditing(scope.row.id)">审批</el-button>
          <!-- 查看40：在驳回页签下显示，可以查看已驳回申请的驳回原因 -->
          <el-button type="primary" link v-if="type==40"
                     @click="auditing(scope.row.id)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <!-- 审批/查看弹窗 -->
  <el-dialog :title="dialogTitle" v-model="auditDialogVisible">
    <el-descriptions direction="horizontal" :column="2" border>
      <el-descriptions-item label="用车人">{{auditdialogData.username}}</el-descriptions-item>
      <el-descriptions-item label="用车事由">{{auditdialogData.reason}}</el-descriptions-item>
      <el-descriptions-item label="使用开始时间">{{auditdialogData.startTime}}</el-descriptions-item>
      <el-descriptions-item label="使用结束时间">{{auditdialogData.endTime}}</el-descriptions-item>
      <el-descriptions-item label="车辆出发地">{{auditdialogData.departureAddr}}</el-descriptions-item>
      <el-descriptions-item label="车辆目的地">{{auditdialogData.destinationAddr}}</el-descriptions-item>
      <el-descriptions-item label="驾照图片">
        <img :src="BASE_URL+auditdialogData.imgUrl" style="width:150px;">
      </el-descriptions-item>
      <el-descriptions-item label="备注">{{auditdialogData.remark}}</el-descriptions-item>
      <!--  驳回原因要在点击驳回才显示 -->
      <el-descriptions-item label="驳回原因" v-if="auditdialogData.auditStatus==40">
        {{auditdialogData.rejectReason}}
      </el-descriptions-item>
    </el-descriptions>
    <template #footer v-if="auditdialogData.auditStatus==10">
      <el-button @click="auditDialogVisible=false">取消</el-button>
      <el-button type="primary" plain @click="rejectInnerDialogVisible=true">驳回</el-button>
      <el-button type="primary" @click="auditPass">通过</el-button>
    </template>
  </el-dialog>

  <!-- 驳回原因弹窗 -->
  <el-dialog title="驳回 查看" v-model="rejectInnerDialogVisible" style="margin-top: 37vh;"
             :before-close="handleCloseInnerDialog">
    <el-descriptions direction="horizontal" border>
      <el-descriptions-item label="驳回原因">
        <el-input placeholder="请输入驳回原因" v-model="rejectReason"></el-input>
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button type="primary" plain @click="handleCloseInnerDialog">取消</el-button>
      <el-button type="primary" @click="auditReject">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import {onMounted, ref} from "vue";
import axios from "axios";
import qs from "qs";
import {ElMessage} from "element-plus";

//控制审批弹窗标题
const dialogTitle = ref("待审批详情");
//控制审批弹窗是否显示
const auditDialogVisible = ref(false);
//控制驳回原因弹窗是否显示
const rejectInnerDialogVisible = ref(false);

//定义数组用来保存审批单列表数据
const auditArr = ref([]);
//定义对象用来保存审批单查询条件
const search = ref({
  username:'' //搜索卡片中的用车人姓名
});
//获取当前登录人的数据
const user = ref(getUser());
//定义一个变量用来保存审批单状态
const type = ref('10');
//定义加载审批单的方法
const loadAudit = () => {
  search.value.auditUserId = user.value.id;
  search.value.auditStatus = type.value;
  console.log(search.value);
  let data = qs.stringify(search.value);
  axios.get(BASE_URL+'/v1/audit/select?'+data).then((response)=>{
    if(response.data.code == 2000){
      auditArr.value = response.data.data;
    }else{
      ElMessage.error(response.data.message);
    }
  })
}
onMounted(()=>{
  loadAudit();
})

//定义重置搜索的方法
const resetSearch = ()=>{
  search.value.username = '';
  loadAudit();
}
//定义对象用来保存审批弹窗数据
const auditdialogData = ref({
  username:'',
  reason:'',
  startTime:'',
  endTime:'',
  departureAddr:'',
  destinationAddr:'',
  imgUrl:'',
  remark:''
});
//定义点击审批按钮调用的方法
const auditing = (id) => {
  auditDialogVisible.value = true;
  axios.get(BASE_URL+'/v1/audit/select?id='+id).
  then((response)=>{
    if(response.data.code == 2000){
      auditdialogData.value = response.data.data[0];
      //根据请求回来的审批单状态修改弹窗标题
      if(auditdialogData.value.auditStatus == 10){
        dialogTitle.value = '待审批详情';
      }else{
        dialogTitle.value = '驳回详情';
      }
    }else{
      ElMessage.error(response.data.message);
    }
  })
}

//定义审批通过的方法
const auditPass = ()=>{
  //将当前的审批状态设置为30已通过
  auditdialogData.value.auditStatus = 30;
  let data = qs.stringify(auditdialogData.value);
  axios.post(BASE_URL+'/v1/audit/update',data).then((response)=>{
    if (response.data.code === 2000){
      ElMessage.success('审批通过!');
      auditdialogData.value = {};//清除审批弹窗数据
      auditDialogVisible.value = false;//关闭审批弹窗
      loadAudit();//刷新审批列表，刚刚通过的那条审批单从 “待我审核”去到“已审核”了
    }else{
      ElMessage.error(response.data.message);
    }
  })
}
//定义变量用来保存驳回原因
const rejectReason = ref('');
//定义审批驳回的方法
const auditReject = ()=>{
  if(!rejectReason.value || rejectReason.value.trim()==''){
    ElMessage.error('请输入驳回原因!');
    return;
  }
  auditdialogData.value.auditStatus = 40;
  auditdialogData.value.rejectReason = rejectReason.value;
  let data = qs.stringify(auditdialogData.value);
  axios.post(BASE_URL+'/v1/audit/update',data).then((response)=>{
    if (response.data.code === 2000){
      ElMessage.success('驳回成功!');
      rejectReason.value = '';//清空驳回原因
      rejectInnerDialogVisible.value = false;//关闭内层弹窗
      auditdialogData.value = {};//清除审批弹窗数据
      auditDialogVisible.value = false;//关闭审批弹窗
      loadAudit();//刷新审批列表，刚刚驳回的那条审批单从 “待我审核”去到“驳回”了
    }else{
      ElMessage.error(response.data.message);
    }
  })
}

//定义处理内层驳回原因弹窗关闭的方法
const handleCloseInnerDialog = () => {
  if(confirm('您确认要取消驳回吗?')){
    rejectReason.value = '';//驳回原因清空
    rejectInnerDialogVisible.value = false;//再关闭内层弹窗
  }
}
</script>

<style>

</style>