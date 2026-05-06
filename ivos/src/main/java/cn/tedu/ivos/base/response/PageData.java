package cn.tedu.ivos.base.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class PageData {
    @Schema(description = "总记录数")
    private Integer total;
    @Schema(description = "当前页数据")
    private List list;
}
