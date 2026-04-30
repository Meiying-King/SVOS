package cn.tedu.ivos.geofence.pojo.vo;

import cn.tedu.ivos.vehicle.pojo.vo.VehicleVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GeofenceVO {
    private Long id; // 电子围栏编号
    private String name; // 电子围栏名称
    private String status; // 电子围栏状态
    private String position; // 电子围栏位置数据
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime; // 创建时间
    /* 在实际开发中，有一些字段是根据业务需要，并不需要持久化保存在数据库中的数据
    * 我们在下方添加的这几个属性可以封装围栏相关的车辆数据，并随VO返回给前端展示 */
    private Integer totalNum;//围栏绑定的车辆总数
    private Integer availableNum;//围栏可用车辆总数
    private List<VehicleVO> vehicleList;//围栏绑定的车辆列表
}







