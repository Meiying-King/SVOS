package cn.tedu.ivos.vehicle.controller;

import cn.tedu.ivos.base.response.JsonResult;
import cn.tedu.ivos.vehicle.pojo.dto.VehicleQuery;
import cn.tedu.ivos.vehicle.pojo.dto.VehicleSaveParam;
import cn.tedu.ivos.vehicle.pojo.vo.VehicleVO;
import cn.tedu.ivos.vehicle.service.VehicleService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/vehicle")
@Tag(name = "车辆模块")
@Slf4j
public class VehicleController {
    @Autowired
    VehicleService vehicleService;
    @GetMapping("select")
    @Operation(summary = "查询车辆")
    @ApiOperationSupport(order = 10)
    public JsonResult selectVehicle(VehicleQuery vehicleQuery){
        log.debug("查询车辆,参数:{}",vehicleQuery);
        List<VehicleVO> list = vehicleService.selectVehicle(vehicleQuery);
        return JsonResult.ok(list);
    }

    @PostMapping("save")
    @Operation(summary = "保存车辆")
    @ApiOperationSupport(order = 20)
    public JsonResult saveVehicle(@Validated VehicleSaveParam vehicleSaveParam){
        log.debug("保存车辆,参数:{}",vehicleSaveParam);
        vehicleService.saveVehicle(vehicleSaveParam);
        return JsonResult.ok();
    }

    @Operation(summary = "删除车辆")
    @ApiOperationSupport(order = 30)
    @PostMapping("delete/{id}")
    public JsonResult deleteVehicle(@PathVariable Long id) {
        log.debug("删除车辆,id={}", id);
        vehicleService.deleteVehicle(id);
        return JsonResult.ok();
    }

    @Operation(summary = "解绑车辆")
    @ApiOperationSupport(order = 40)
    @PostMapping("unbind/{vehicleId}")
    public JsonResult unbindVehicle(@PathVariable Long vehicleId) {
        log.debug("解绑车辆,vehicleId={}", vehicleId);
        vehicleService.unbindVehicle(vehicleId);
        return JsonResult.ok();
    }

    @Operation(summary = "绑定车辆")
    @ApiOperationSupport(order = 50)
    @PostMapping("bind/{geofenceId}/{vehicleId}")
    public JsonResult unbindVehicle(
            @PathVariable Long geofenceId,@PathVariable Long vehicleId) {
        log.debug("绑定车辆,geofenceId={},vehicleId={}", geofenceId,vehicleId);
        vehicleService.bindVehicle(geofenceId,vehicleId);
        return JsonResult.ok();
    }

}
