package cn.tedu.ivos.dict.controller;

import cn.tedu.ivos.base.response.JsonResult;
import cn.tedu.ivos.base.response.PageData;
import cn.tedu.ivos.dict.pojo.dto.DictQuery;
import cn.tedu.ivos.dict.pojo.dto.DictSaveParam;
import cn.tedu.ivos.dict.pojo.vo.DictVO;
import cn.tedu.ivos.dict.service.DictService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "字典模块")
@Slf4j
@RestController
@RequestMapping("/v1/dict")
public class DictController {
    @Autowired
    DictService dictService;

    @Operation(summary = "查询字典")
    @ApiOperationSupport(order = 10)
    @GetMapping("select")
    public JsonResult selectDict(DictQuery dictQuery){
        log.debug("查询字典,参数:{}",dictQuery);
//        List<DictVO> list = dictService.selectDict(dictQuery);
        PageData data=dictService.selectDict(dictQuery);
        return JsonResult.ok(data);
    }

    @Operation(summary = "保存字典")
    @ApiOperationSupport(order = 20)
    @PostMapping("save")
    public JsonResult saveDict(@Validated DictSaveParam dictSaveParam){
        log.debug("保存字典,参数:{}",dictSaveParam);
        dictService.saveDict(dictSaveParam);
        return JsonResult.ok();
    }

    @Operation(summary = "删除字典")
    @ApiOperationSupport(order = 30)
    @PostMapping("delete/{id}")
    public JsonResult deleteDict(@PathVariable Long id){
        log.debug("删除字典,参数:{}",id);
        dictService.deleteDict(id);
        return JsonResult.ok();
    }



}
