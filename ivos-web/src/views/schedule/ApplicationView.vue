<!--用车申请页-->
<template>
  <div style="height: 6vh;background-color:#fff;padding:10px 20px;">
    <span style="line-height:60px;font-size:20px;">申请单列表</span>
    <el-button type="primary"
               style="float:right;margin-top:13px;"
               @click="addApplicationDialogVisible=true"
               :disabled="user.parentId==null"
    >
      申请用车
    </el-button>
    <!-- v-if="user.parentId!=null"-->
  </div>
  <!-- 申请用车弹窗 -->
  <el-dialog title="创建申请单" v-model="addApplicationDialogVisible"
             style="width:1000px;padding:40px;" :before-close="handleClose">
    <el-form label-width="80px" label-position="top">
      <el-row :gutter="30">
        <el-col :span="12">
          <el-form-item label="用车人">
            <el-input readonly :value="user.username"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="使用时间">
            <el-date-picker
                type="datetimerange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD HH:mm:ss"
                format="YYYY-MM-DD HH:mm:ss"
                v-model="times"
            ></el-date-picker>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="30">
        <el-col :span="12">
          <el-form-item label="车辆出发地">
            <el-input placeholder="请输入" v-model="addForm.departureAddr"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="车辆目的地">
            <el-input placeholder="请输入" v-model="addForm.destinationAddr"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="30">
        <el-col :span="12">
          <el-form-item label="用车事由">
            <el-input placeholder="请输入" type="textarea" resize="none" :rows="3" v-model="addForm.reason"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="备注">
            <el-input placeholder="请输入" type="textarea" resize="none" :rows="3" v-model="addForm.remark"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="30">
        <el-col :span="12">
          <el-form-item label="驾照">
            <el-upload
                v-model:file-list="fileList"
                v-model:action="actionUrl"
                name="file"
                limit="1"
                list-type="picture-card"
                :on-preview="handlePictureCardPreview"
                :on-remove="handleRemove"
            >
              <el-icon>
                <Plus/>
              </el-icon>
            </el-upload>
            <el-dialog v-model="dialogVisible">
              <img w-full :src="dialogImageUrl"/>
            </el-dialog>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="审批人">
            <el-row :gutter="10">
              <el-col :span="12" v-if="auditUserOpts.length>0">
                <el-tag type="primary">
                  <el-icon style="margin-right:5px;position:relative;top:2px;">
                    <User/>
                  </el-icon>
                  <span>{{ auditUserOpts[0].username }}</span>
                </el-tag>
              </el-col>
              <el-col :span="12" v-if="auditUserOpts.length>1">
                <el-tag type="primary">
                  <el-icon style="margin-right:5px;position:relative;top:2px;">
                    <User/>
                  </el-icon>
                  <span>{{ auditUserOpts[1].username }}</span>
                </el-tag>
              </el-col>
            </el-row>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="addApplication">确定</el-button>
    </template>
  </el-dialog>

  <!-- 用车申请列表 搜索卡片 -->
  <el-card style="margin: 20px;">
    <el-form style="padding-top:10px;">
      <el-row :gutter="30">
        <el-col :span="5">
          <el-form-item label="出发地">
            <el-input placeholder="请输入出发地" v-model="searchApplication.departureAddr"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item label="目的地">
            <el-input placeholder="请输入目的地" v-model="searchApplication.destinationAddr"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item label="申请单状态">
            <el-select placeholder="请选择" v-model="searchApplication.status">
              <el-option v-for="item in applicationStatusArr"
                         :value="item.value" :label="item.label"></el-option>
<!--              <el-option label="已发起" value="10"></el-option>-->
<!--              <el-option label="撤销" value="20"></el-option>-->
<!--              <el-option label="审核中" value="30"></el-option>-->
<!--              <el-option label="驳回" value="40"></el-option>-->
<!--              <el-option label="已通过" value="50"></el-option>-->
<!--              <el-option label="分配用车" value="60"></el-option>-->
<!--              <el-option label="工单结束" value="70"></el-option>-->
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item>
            <el-button @click="resetSearch">重置</el-button>
            <el-button type="primary" @click="loadApplication">查询</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </el-card>
  <!-- 用车申请表格 -->
  <el-card style="margin:20px">
    <el-table :data="tableData">
      <el-table-column type="index" width="80" align="center" prop="id" label="编号"></el-table-column>
      <el-table-column align="center" prop="username" label="申请人"></el-table-column>
      <el-table-column align="center" prop="departureAddr" label="出发地"></el-table-column>
      <el-table-column align="center" prop="destinationAddr" label="目的地"></el-table-column>
      <el-table-column align="center" prop="reason" label="用车原因"></el-table-column>
      <el-table-column align="center" prop="auditUsernameList" label="审批人"></el-table-column>
      <el-table-column align="center" prop="startTime" label="使用开始时间"></el-table-column>
      <el-table-column align="center" prop="endTime" label="使用结束时间"></el-table-column>
      <el-table-column align="center" prop="status" label="申请单状态"
                       :formatter="applicationStatusFormatter"></el-table-column>
      <el-table-column label="操作" width="120" align="center">
        <template #default="scope">
          <el-button type="primary" size="small" link
                     :disabled="scope.row.status!=10"
                     @click="cancel(scope.row.id)">撤销</el-button>
        </template>
      </el-table-column>
    </el-table>

  </el-card>
</template>
<script setup>
import {onMounted, ref} from "vue";
import axios from "axios";
import {ElMessage} from "element-plus";
import qs from "qs";
//定义变量控制创建申请单弹窗是否出现
const addApplicationDialogVisible = ref(false);


/**图片上传相关代码开始**/
const fileList = ref([]);
const dialogImageUrl = ref('');
const dialogVisible = ref(false);
//定义变量用来保存图片上传给哪个后端接口地址
const actionUrl = ref(BASE_URL + '/v1/file/upload');
//移除图片
const handleRemove = (uploadFile, uploadFiles) => {
  console.log(uploadFile, uploadFiles);
  //1.得到要删除的图片路径
  let imgUrl = fileList.value[0].response.data;
  //2.给后端发请求,删除图片
  axios.post(BASE_URL+'/v1/file/remove?imgUrl='+imgUrl)
      .then((response)=>{
        if (response.data.code==2000){
          // ElMessage.success('删除成功!');
          console.log('删除成功!');
        }
      })
}
//图片上传后预览
const handlePictureCardPreview = (uploadFile) => {
  dialogImageUrl.value = uploadFile.url
  dialogVisible.value = true;
  console.log(uploadFile);
  //console.log(fileList.value[0].response.data);
}
/**图片上传相关代码结束**/

//获取当前登录的用户数据
const user = ref(getUser());
//定义数组用来保存多个审批人对象
const auditUserOpts = ref([]);
//定义数组用来保存多个审批人id(创建多条审批单入库时使用的)
const auditUserIdList = ref([]);
//定义加载当前申请人(登录人)对应的多个审批人的方法
const loadAuditUsers = ()=>{
  //获取当前登录人的直属领导编号
  let parentId = user.value.parentId;
  //给后端发请求
  axios.get(BASE_URL+'/v1/user/select/audit/'+parentId)
      .then((response)=>{
        if(response.data.code == 2000){
          auditUserOpts.value = response.data.data;
          //还需要将拿到的审批人数组进行遍历,取出每个审批人的id存入数组中
          for(let i = 0; i < auditUserOpts.value.length; i++){
            auditUserIdList.value.push(auditUserOpts.value[i].id);
          }
        }else{
          ElMessage.error(response.data.message);
        }
      })
}
onMounted(()=>{
  loadAuditUsers();
})

//定义数组用来保存申请用车的起止时间
const times = ref([]);
//定义对象用来保存弹窗表单数据
const addForm = ref({
  departureAddr: '',
  destinationAddr: '',
  reason: '',
  remark:''
});
//定义新增申请的方法
const addApplication = () => {
  //需要对图片数组做非空校验,如果未上传图片,直接提示用户
  if(!fileList.value.length>0){
    ElMessage.error('请上传图片!');
    return;
  }
  addForm.value.imgUrl = fileList.value[0].response.data;
  addForm.value.startTime = times.value[0];
  addForm.value.endTime = times.value[1];
  addForm.value.username = user.value.username;
  addForm.value.userId = user.value.id;
  addForm.value.auditUserIdList = auditUserIdList.value;
  console.log(addForm.value);
  let data = qs.stringify(addForm.value);
  axios.post(BASE_URL+'/v1/application/save',data).then((response)=>{
    if(response.data.code == 2000){
      ElMessage.success('申请成功!');
      fileList.value = [];//清空图片上传列表
      times.value = [];//清空时间选择器
      addForm.value = {};//清空弹窗表单数据
      addApplicationDialogVisible.value = false;
    }else if(response.data.code == 3001){
      ElMessage.error(response.data.data);
    }else{
      ElMessage.error(response.data.message);
    }
  })
}
//定义关闭新增申请弹窗的方法
const handleClose = () => {
  if(confirm('您确认要取消新增吗?')){
    times.value = [];//清空时间选择器
    addForm.value = {};//清空弹窗表单数据
    //如果用户上传了图片,又要取消申请,也需要将服务器中的图片删除
    if(fileList.value.length>0){
      handleRemove();//调用方法给后端发请求删除服务器上的图片
      fileList.value = [];//清空图片上传列表
    }
    addApplicationDialogVisible.value = false;//关闭弹窗
  }
}

//定义对象用来保存申请单查询条件
const searchApplication = ref({
  departureAddr:'',
  destinationAddr:'',
  status:''
});
//定义数组用来保存申请单列表
const tableData = ref([]);
//定义加载申请单的方法
const loadApplication = ()=>{
  let data = qs.stringify(searchApplication.value);
  axios.get(BASE_URL+'/v1/application/select?'+data).then((response)=>{
    if(response.data.code == 2000){
      tableData.value = response.data.data;
    }else{
      ElMessage.error(response.data.message);
    }
  })
}
//定义重置搜索条件的方法
const resetSearch = () => {
  searchApplication.value = {};
  loadApplication();
}
//定义数组用来保存申请单状态字典项
const applicationStatusArr = ref([]);
//一进入页面,首次加载申请单列表
onMounted(()=>{
  loadApplication();
  loadDictOptions(applicationStatusArr, 'application_status')
})
//定义处理申请单状态表格列显示文字转换的方法 20=>撤销
const applicationStatusFormatter = (row, column, cellValue, index) => {
  if(!applicationStatusArr.value){
    return cellValue;
  }
  for(let item of applicationStatusArr.value){
    if(item.value == cellValue){
      cellValue = item.label;
    }
  }
  return cellValue;
}
//定义撤销申请的方法
const cancel = (id) => {
  if(confirm('您确认要撤销申请吗?')){
    axios.post(BASE_URL+'/v1/application/cancel/'+id)
        .then((response)=>{
      if(response.data.code == 2000){
        ElMessage.success('撤销成功!');
        loadApplication();//重新加载所有申请单
      }else{
        ElMessage.error(response.data.message);
      }
    })
  }
}

</script>

<style scoped>
</style>