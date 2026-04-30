package cn.tedu.ivos.audit.service;

import cn.tedu.ivos.application.pojo.entity.Application;
import cn.tedu.ivos.audit.pojo.dto.AuditQuery;
import cn.tedu.ivos.audit.pojo.dto.AuditSaveParam;
import cn.tedu.ivos.audit.pojo.vo.AuditVO;

import java.util.List;

public interface AuditService {
    void insertAudit(Application application);

    List<AuditVO> selectAuditByApplicationId(Long id);

    List<AuditVO> selectAudit(AuditQuery auditQuery);

    void updateAudit(AuditSaveParam auditSaveParam);
}
