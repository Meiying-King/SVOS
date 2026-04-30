package cn.tedu.ivos.audit.controller;

import cn.tedu.ivos.audit.pojo.dto.AuditQuery;
import cn.tedu.ivos.audit.pojo.dto.AuditSaveParam;
import cn.tedu.ivos.audit.pojo.vo.AuditVO;
import cn.tedu.ivos.audit.service.AuditService;
import cn.tedu.ivos.base.response.JsonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "审批模块")
@Slf4j
@RestController
@RequestMapping("/v1/audit")
public class AuditController {
    @Autowired
    AuditService auditService;

    @Operation(summary = "查询审批单列表")
    @ApiOperationSupport(order = 10)
    @GetMapping("select")
    public JsonResult selectAudit(AuditQuery auditQuery){
        log.debug("查询审批单列表,参数:{}",auditQuery);
        List<AuditVO> list = auditService.selectAudit(auditQuery);
        return JsonResult.ok(list);
    }

    @Operation(summary = "审批申请单")
    @ApiOperationSupport(order = 20)
    @PostMapping("update")
    public JsonResult updateAudit(AuditSaveParam auditSaveParam){
        log.debug("更新审批单,参数:{}",auditSaveParam);
        auditService.updateAudit(auditSaveParam);
        return JsonResult.ok();
    }

}
