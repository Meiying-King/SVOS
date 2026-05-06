package cn.tedu.ivos.dict.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DictQuery {
    @Schema(description = "字典编号")
    private Long id;
    @Schema(description = "字典名称")
    private String name;
    @Schema(description = "字典编码")
    private String code;

    @Schema(description = "每页记录数")
    private Integer pageSize;
    @Schema(description = "当前页码")
    private Integer pageNum;
}
