<!-- 围栏地图页 -->
<template>
  <div style="height: 6vh;background-color:#fff;padding:10px 20px;">
    <span style="line-height:60px;font-size:20px;">新增围栏</span>
    <el-button type="primary" style="float:right;margin-top:13px;"
               @click="router.push('/geofence')">返回</el-button>
  </div>
  <!-- 创建一个div充当地图容器 -->
  <div id="mapContainer" style="width:100%;height: 84vh;"></div>

  <!-- 创建新建围栏弹窗 -->
  <el-dialog title="新建围栏" v-model="dialogVisible" :before-close="closeDialog">
    <el-form label-position="top">
      <el-form-item label="围栏名称">
        <el-input placeholder="请输入围栏名称" v-model="geofence.name"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="closeDialog">取消</el-button>
      <el-button type="primary" @click="saveGeofence">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import router from "@/router";
import {nextTick, onMounted, ref} from "vue";
import { DrawScene, DrawControl,OperateEventType } from 'bmap-draw';
import {ElMessage} from "element-plus";
import qs from "qs";
import axios from "axios";

//7.准备一个电子围栏对象用于保存给后端发送新增围栏请求时的参数
const geofence = ref({
  name:'',//围栏名称
  position:{} //围栏位置数据,注意:它也是一个对象!
});

//定义全局变量用来保存地图对象
var map;

//一进入页面立即加载地图
onMounted(()=>{
  //表示DOM渲染完毕后立即执行的方法
  //1.因为我们要确保上面的地图容器div已成功创建,才能往div里装地图
  nextTick(()=>{
    //2.创建地图对象,不需要导包!
    map = new BMapGL.Map("mapContainer");
    //3.设置地图中心点,此处是天安门的经纬度
    let point = new BMapGL.Point(116.4074, 39.9024);
    //4.设置地图对象的中心点与缩放级别
    map.centerAndZoom(point, 15);

    //5.添加鼠标绘制控件
    //5.1 创建鼠标绘制场景类,这个类是其他绘制功能的基类,负责事件的监听与覆盖物的存取
    const scene = new DrawScene(map);
    //5.2 创建绘制控件对象,创建时需要传入绘制场景对象与它自己的配置对象
    const drawContrl = new DrawControl(scene, {
      //5.3 配置了:不显示用途提示 + 定位在右上角 + 可绘制圆形与矩形
      enableTips: false,
      anchor: BMAP_ANCHOR_TOP_RIGHT,
      drawingItems: [
        'circle',
        'rectangle'
      ]
    });
    //6.监听绘制完成事件,获取触发完成事件的电子围栏目标对象,注意需要导入OperateEventType!
    scene.addEventListener(OperateEventType.COMPLETE, (event) => {
      //9.只要用户绘制完成(点"√")时,就出输入围栏名称的弹窗!
      dialogVisible.value = true;
      //event是完成事件的返回值,DOM中的所有事件对象都基于Event对象
      //event.target 用于获取当前触发完成事件的围栏元素
      console.log(event.target);
      /** 8.获取围栏位置数据 */
      //8.1 获取目标对象中的覆盖物
      let overlay = event.target.overlay;
      //8.2 判断是圆还是方,分别处理
      if(overlay instanceof BMapGL.Circle){//是圆形
        //8.3 给上面的围栏对象的位置属性赋值
        geofence.value.position.type = 'circle';//围栏类型为圆形
        geofence.value.position.longitude = overlay.getCenter().lng;//圆心经度
        geofence.value.position.latitude = overlay.getCenter().lat;//圆心纬度
        geofence.value.position.radius = overlay.getRadius();//半径
        console.log(geofence.value);
      }else{//是方形
        //8.4 设置围栏类型为矩形
        geofence.value.position.type = 'rectangle';
        //8.5 准备数组用来存放矩形围栏的4个顶点坐标
        let recPoints = [];
        //8.6 循环4次,依次将每个顶点存入数组中
        for(let i = 0; i < 4; i++){
          recPoints.push(overlay.points[i].latLng.lng +'-'
              + overlay.points[i].latLng.lat);
        }
        geofence.value.position.recPoints = recPoints.join(',');
        console.log(geofence.value);
      }

    });
    //5.4 将创建好的绘制控件对象添加到地图对象中
    map.addControl(drawContrl);
  })
})

//定义变量用来控制弹窗是否出现
const dialogVisible = ref(false);
//定义处理关闭弹窗的方法
const closeDialog = () => {
  if(confirm('您确认要取消新增围栏吗?')){
    //注意:position是一个对象{},不能写成'' !!!
    geofence.value = {name:'',position:{}};//清空弹窗输入的围栏名称
    //清除地图上不要的围栏覆盖物
    map.clearOverlays();
    dialogVisible.value = false;//关闭弹窗
  }
}
//定义保存围栏的方法
const saveGeofence = () => {
  if(!geofence.value.name || geofence.value.name.trim()==''){
    ElMessage.error('请输入围栏名称!');
    return;
  }
  //由于geofence对象中的position也是一个对象,而数据库中存的是JSON串,所以我们需要先处理下
  geofence.value.position = JSON.stringify(geofence.value.position);
  let data = qs.stringify(geofence.value);//再整体将geofence对象处理一下
  axios.post(BASE_URL+'/v1/geofence/save',data)
      .then((response)=>{
        if (response.data.code === 2000){
          ElMessage.success('新增围栏成功!');
          dialogVisible.value = false;//关闭弹窗
          router.push('/geofence');//跳转到围栏列表页面
        }else {
          ElMessage.error(response.data.message);
        }
      })
}
</script>

<style scoped>

</style>

<!--<template>-->
<!--  <div style="height: 6vh;background-color:#fff;padding:10px 20px;">-->
<!--    <span style="line-height:60px;font-size:20px;">新增围栏</span>-->
<!--    <el-button type="primary" style="float:right;margin-top:13px;"-->
<!--               @click="router.push('/geofence')">返回</el-button>-->
<!--  </div>-->
<!--  <div id="mapContainer" style="width:100%;height:84vh;"></div>-->
<!--</template>-->
<!--<script setup>-->
<!--  import router from "@/router";-->
<!--  import {nextTick, onMounted} from "vue";-->
<!--  import { DrawScene, DrawControl } from 'bmap-draw';-->
<!--//一进到页面立即加载地图-->
<!--onMounted(()=>{-->
<!--  //表示DOM更新完毕后立即执行的方法,因为我们要确保上方的地图容器div已创建,才能往div里装地图-->
<!--  nextTick(()=>{-->
<!--    //创建地图实例,注意:忽略导入语句!不需要导入!-->
<!--    let map = new BMapGL.Map("mapContainer");-->
<!--    //设置地图展示的中心点,此处的坐标是天安门的经纬度-->
<!--    let point = new BMapGL.Point(116.4074, 39.9024);-->
<!--    //设置地图对象的中心点与缩放级别-->
<!--    map.centerAndZoom(point, 15);-->

<!--    // 添加鼠标绘制控件-->
<!--    const scene = new DrawScene(map);-->
<!--    const drawContrl = new DrawControl(scene, {-->
<!--      enableTips: false,-->
<!--      anchor: BMAP_ANCHOR_TOP_RIGHT,-->
<!--      drawingItems: [-->
<!--        'marker',-->
<!--        'polyline',-->
<!--        'polygon',-->
<!--        'circle',-->
<!--        'rectangle'-->
<!--      ]-->
<!--    });-->
<!--    map.addControl(drawContrl);-->
<!--  })-->
<!--})-->
<!--</script>-->

<!--<style scoped>-->
<!--</style>-->