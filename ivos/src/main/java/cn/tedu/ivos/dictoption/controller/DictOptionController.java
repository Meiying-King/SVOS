package cn.tedu.ivos.dictoption.controller;

import cn.tedu.ivos.base.response.JsonResult;
import cn.tedu.ivos.dictoption.pojo.dto.DictOptionQuery;
import cn.tedu.ivos.dictoption.pojo.dto.DictOptionSaveParam;
import cn.tedu.ivos.dictoption.pojo.vo.DictOptionVO;
import cn.tedu.ivos.dictoption.service.DictOptionService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "字典项模块")
@Slf4j
@RestController
@RequestMapping("/v1/dictoption")
public class DictOptionController {
    @Autowired
    DictOptionService dictOptionService;

    @Operation(summary = "查询字典项")
    @ApiOperationSupport(order = 10)
    @GetMapping("select")
    public JsonResult selectDictOption(DictOptionQuery dictOptionQuery){
        log.debug("查询字典项,参数:{}",dictOptionQuery);
        List<DictOptionVO> list = dictOptionService.selectDictOption(dictOptionQuery);
        return JsonResult.ok(list);
    }

    @Operation(summary = "保存字典项")
    @ApiOperationSupport(order = 20)
    @PostMapping("save")
    public JsonResult saveDictOption(@Validated DictOptionSaveParam dictOptionSaveParam){
        log.debug("保存字典项,参数:{}",dictOptionSaveParam);
        dictOptionService.saveDictOption(dictOptionSaveParam);
        return JsonResult.ok();
    }

    @Operation(summary = "删除字典项")
    @ApiOperationSupport(order = 30)
    @PostMapping("delete/{id}")
    public JsonResult deleteDictOption(@PathVariable Long id){
        log.debug("删除字典项,参数:{}",id);
        dictOptionService.deleteDictOption(id);
        return JsonResult.ok();
    }

    @Operation(summary = "根据字典code查其对应所有字典项")
    @ApiOperationSupport(order = 40)
    @GetMapping("select/{code}")
    public JsonResult selectDictOptionByCode(@PathVariable String code){
        log.debug("根据字典code查其对应所有字典项,参数:{}",code);
        List<DictOptionVO> list = dictOptionService.selectDictOptionByCode(code);
        return JsonResult.ok(list);
    }





}
