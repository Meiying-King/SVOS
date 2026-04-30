package cn.tedu.ivos.vehicle.service.impl;

import cn.tedu.ivos.base.exception.ServiceException;
import cn.tedu.ivos.base.response.StatusCode;
import cn.tedu.ivos.vehicle.mapper.VehicleMapper;
import cn.tedu.ivos.vehicle.pojo.dto.VehicleQuery;
import cn.tedu.ivos.vehicle.pojo.dto.VehicleSaveParam;
import cn.tedu.ivos.vehicle.pojo.entity.Vehicle;
import cn.tedu.ivos.vehicle.pojo.vo.VehicleVO;
import cn.tedu.ivos.vehicle.service.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class VehicleServiceImpl implements VehicleService {
    @Autowired
    VehicleMapper vehicleMapper;
    @Override
    public List<VehicleVO> selectVehicle(VehicleQuery vehicleQuery) {
       log.debug("查询车辆,参数:{}",vehicleQuery);
       List<VehicleVO> list = vehicleMapper.selectVehicle(vehicleQuery);
       return list;
    }

    @Override
    public void saveVehicle(VehicleSaveParam vehicleSaveParam) {
        log.debug("保存车辆业务,参数:{}",vehicleSaveParam);
        Vehicle vehicle = new Vehicle();
        BeanUtils.copyProperties(vehicleSaveParam,vehicle);
        //判断车牌号是否已经存在
        VehicleVO vehicleVO = vehicleMapper.selectVehicleByLicense(vehicle.getLicense());
        if (vehicle.getId() == null){//新增操作
            //说明根据前端传过来的车牌号,查出来车了
            if (vehicleVO != null){
                //抛出业务异常:车牌号已存在,不能新建车辆!
                throw new ServiceException(StatusCode.LICENSE_EXISTS);
            }
            //如果车牌号不存在,正常新增车辆
            vehicle.setCreateTime(new Date());
            vehicle.setStatus("1");//车辆空闲
            vehicle.setGeofenceBindStatus("0");//车辆默认未绑定电子围栏
            vehicleMapper.insert(vehicle);
        } else {//更新操作
            //vehicleVO是根据前端车牌号查出来的车
            //所以:如果根据车牌号查出来车,并且车牌号对应的车辆id与你当前要修改的车辆id不一致
            //说明要改的这个车牌号是数据库里别人已存在的车牌号
            if (vehicleVO != null && vehicleVO.getId() != vehicle.getId()){
                //抛出业务异常:车牌号已存在,不能修改车辆!
                throw new ServiceException(StatusCode.LICENSE_EXISTS);
            }
            //如果车牌号不存在,正常更新车辆
            vehicle.setUpdateTime(new Date());
            vehicleMapper.update(vehicle);
        }
    }

    @Override
    public void deleteVehicle(Long id) {
        log.debug("删除车辆业务:id={}",id);
        vehicleMapper.deleteById(id);
    }

    @Override
    public void unbindVehicle(Long vehicleId) {
        log.debug("解绑车辆业务:vehicleId={}",vehicleId);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setGeofenceBindStatus("0");
        vehicle.setGeofenceId(null);
        vehicle.setUpdateTime(new Date());
        vehicleMapper.updateNullValue(vehicle);
    }

    @Override
    public void bindVehicle(Long geofenceId, Long vehicleId) {
        log.debug("绑定车辆业务:geofenceId={},vehicleId={}",geofenceId,vehicleId);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setGeofenceBindStatus("1");
        vehicle.setGeofenceId(geofenceId);
        vehicle.setUpdateTime(new Date());
        vehicleMapper.update(vehicle);
    }
}
