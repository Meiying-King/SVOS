package cn.tedu.ivos.geofence.controller;

import cn.tedu.ivos.base.response.JsonResult;
import cn.tedu.ivos.geofence.pojo.dto.GeofenceQuery;
import cn.tedu.ivos.geofence.pojo.dto.GeofenceSaveParam;
import cn.tedu.ivos.geofence.pojo.vo.GeofenceVO;
import cn.tedu.ivos.geofence.service.GeofenceService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "围栏模块")
@Slf4j
@RestController
@RequestMapping("/v1/geofence")
public class GeofenceController {
    @Autowired
    GeofenceService geofenceService;

    @Operation(summary = "查询电子围栏")
    @ApiOperationSupport(order = 10)
    @GetMapping("select")
    public JsonResult selectGeofence(GeofenceQuery geofenceQuery){
        log.debug("查询电子围栏,参数:{}",geofenceQuery);
        List<GeofenceVO> list = geofenceService.selectGeofence(geofenceQuery);
        return JsonResult.ok(list);
    }

    @Operation(summary = "修改电子围栏状态")
    @ApiOperationSupport(order = 20)
    @PostMapping("update/{id}/{status}")
    public JsonResult updateGeofenceStatus(
            @PathVariable Long id,@PathVariable String status){
        log.debug("修改电子围栏状态,id={},status={}",id,status);
        geofenceService.updateStatus(id,status);
        return JsonResult.ok();
    }

    @Operation(summary = "删除电子围栏")
    @ApiOperationSupport(order = 30)
    @PostMapping("delete/{id}")
    public JsonResult deleteGeofence(@PathVariable Long id){
        log.debug("删除电子围栏,id={}",id);
        geofenceService.deleteGeofence(id);
        return JsonResult.ok();
    }

    @Operation(summary = "添加电子围栏")
    @ApiOperationSupport(order = 40)
    @PostMapping("save")
    public JsonResult saveGeofence(@Validated GeofenceSaveParam geofenceSaveParam){
        log.debug("添加电子围栏,参数:{}",geofenceSaveParam);
        geofenceService.saveGeofence(geofenceSaveParam);
        return JsonResult.ok();
    }


}
