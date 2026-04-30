package cn.tedu.ivos.geofence.service.impl;

import cn.tedu.ivos.base.exception.ServiceException;
import cn.tedu.ivos.base.response.StatusCode;
import cn.tedu.ivos.geofence.mapper.GeofenceMapper;
import cn.tedu.ivos.geofence.pojo.dto.GeofenceQuery;
import cn.tedu.ivos.geofence.pojo.dto.GeofenceSaveParam;
import cn.tedu.ivos.geofence.pojo.entity.Geofence;
import cn.tedu.ivos.geofence.pojo.vo.GeofenceVO;
import cn.tedu.ivos.geofence.service.GeofenceService;
import cn.tedu.ivos.vehicle.mapper.VehicleMapper;
import cn.tedu.ivos.vehicle.pojo.dto.VehicleQuery;
import cn.tedu.ivos.vehicle.pojo.vo.VehicleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class GeofenceServiceImpl implements GeofenceService {
    @Autowired
    GeofenceMapper geofenceMapper;
    @Autowired
    VehicleMapper vehicleMapper;

    @Override
    public List<GeofenceVO> selectGeofence(GeofenceQuery geofenceQuery) {
        log.debug("查询电子围栏业务,参数:{}",geofenceQuery);
        List<GeofenceVO> list = geofenceMapper.selectGeofence(geofenceQuery);
        //在此处补全代码，查出围栏相关的车辆数据
        //遍历得到每一个围栏VO,为其补全数据
        for(int i = 0; i < list.size(); i++){
            GeofenceVO geofenceVO = list.get(i);
            //封装车辆的查询条件
            VehicleQuery vehicleQuery = new VehicleQuery();
            vehicleQuery.setGeofenceId(geofenceVO.getId());
            List<VehicleVO> vehicleVOList = vehicleMapper.selectVehicle(vehicleQuery);
            //获取当前围栏上绑定的车辆总数
            int totalNum = vehicleVOList.size();
            //定义变量用来保存可用车辆总数
            int availableNum = 0;
            //遍历车辆列表，依次查看车辆状态，如果为“空闲”状态，则可用车辆数加1
            for (VehicleVO vehicleVO : vehicleVOList){
                if(vehicleVO.getStatus().equals("1")){
                    availableNum++;
                }
            }
            //给当前这个围栏对象补全数据
            geofenceVO.setTotalNum(totalNum);
            geofenceVO.setAvailableNum(availableNum);
            geofenceVO.setVehicleList(vehicleVOList);
        }
        return list;
    }

    @Override
    public void updateStatus(Long id, String status) {
        log.debug("修改电子围栏状态业务,参数:{},{}",id,status);
        Geofence geofence = new Geofence();
        geofence.setId(id);
        geofence.setStatus(status);
        geofence.setUpdateTime(new Date());
        geofenceMapper.update(geofence);
    }

    @Override
    public void deleteGeofence(Long id) {
        log.debug("删除电子围栏业务,参数:{}",id);
        //1.封装车辆查询对象
        VehicleQuery vehicleQuery = new VehicleQuery();
        vehicleQuery.setGeofenceId(id);
        //2.调用车辆模块的功能查询车辆
        List<VehicleVO> list = vehicleMapper.selectVehicle(vehicleQuery);
        //3.如果该围栏上有绑定的车辆,抛出"围栏上存在未移除车辆"异常
        if(list !=null && list.size()>0){
            throw new ServiceException(StatusCode.VHICLE_EXISTS);
        }else{
            //4.如果围栏上没有车辆,正常删除
            geofenceMapper.deleteById(id);
        }
    }

    @Override
    public void saveGeofence(GeofenceSaveParam geofenceSaveParam) {
        log.debug("保存电子围栏业务,参数:{}",geofenceSaveParam);
        Geofence geofence = new Geofence();
        BeanUtils.copyProperties(geofenceSaveParam,geofence);
        geofence.setStatus("1");//围栏默认状态为启用
        geofence.setCreateTime(new Date());
        geofenceMapper.insert(geofence);
    }
}
