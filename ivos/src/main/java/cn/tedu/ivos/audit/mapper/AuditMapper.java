package cn.tedu.ivos.audit.mapper;

import cn.tedu.ivos.audit.pojo.dto.AuditQuery;
import cn.tedu.ivos.audit.pojo.entity.Audit;
import cn.tedu.ivos.audit.pojo.vo.AuditVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditMapper {
    void insert(Audit audit);

    List<AuditVO> selectAuditByApplicationId(Long id);

    void deleteByApplicationId(Long id);

    List<AuditVO> selectAudit(AuditQuery auditQuery);

    void update(Audit audit);

    Integer selectRestAuditCount(AuditQuery auditQuery);

    void deleteById(Long id);
}
