package cn.tedu.ivos.vehicle.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VehicleQuery {
    @Schema(description = "车辆id")
    private Long id;
    @Schema(description = "车辆品牌")
    private String brand;
    @Schema(description = "车牌号")
    private String license;
    @Schema(description = "电子围栏绑定状态")
    private String geofenceBindStatus;
    @Schema(description = "电子围栏id")
    private Long geofenceId;
}
