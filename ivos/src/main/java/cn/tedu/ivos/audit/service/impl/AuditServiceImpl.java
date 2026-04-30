package cn.tedu.ivos.audit.service.impl;

import cn.tedu.ivos.application.mapper.ApplicationMapper;
import cn.tedu.ivos.application.pojo.entity.Application;
import cn.tedu.ivos.audit.mapper.AuditMapper;
import cn.tedu.ivos.audit.pojo.dto.AuditQuery;
import cn.tedu.ivos.audit.pojo.dto.AuditSaveParam;
import cn.tedu.ivos.audit.pojo.entity.Audit;
import cn.tedu.ivos.audit.pojo.vo.AuditVO;
import cn.tedu.ivos.audit.service.AuditService;
import cn.tedu.ivos.base.enums.ApplicationStatusEnum;
import cn.tedu.ivos.base.enums.AuditStatusEnum;
import cn.tedu.ivos.user.mapper.UserMapper;
import cn.tedu.ivos.user.pojo.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

/* @Transactional是Spring提供的一个用于进行事务管理的注解,可以管理类或方法上的事务行为
 * 在我们对数据库做操作的时候,可以确保类中的方法对数据库的操作是在一个事务中进行的,要么都成功,要么都失败 */
@Transactional
@Slf4j
@Service
public class AuditServiceImpl implements AuditService {
    @Autowired
    AuditMapper auditMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ApplicationMapper applicationMapper;

    @Override
    public void insertAudit(Application application) {
        log.debug("新增申请单对应的审核单业务,参数:{}", application);
        //1.获取申请单中的审批人id集合
        List<Long> userIdList = application.getAuditUserIdList();
        //2.遍历id集合,有几个id,就代表有几个审批人
        for (int i = 0; i < userIdList.size(); i++) {
            Audit audit = new Audit();//3.创建空的审批单对象并封装条件
            audit.setApplicationId(application.getId());//申请单id
            audit.setAuditUserId(userIdList.get(i));//审批人id
            audit.setAuditSort(i);//审批次序
            if(i == 0){//如果是第一次循环,代表在创建第一个审批人的审批单,状态是"待我审核"
                audit.setAuditStatus(AuditStatusEnum.MY_PENDING.getCode());
            }else{//如果是后面的审批人,状态是"待他人审核"
                audit.setAuditStatus(AuditStatusEnum.PENDING.getCode());
            }
            audit.setCreateTime(new Date());//创建时间
            auditMapper.insert(audit);//4.将本轮循环准备好的审批单插入数据库
        }
    }

    @Override
    public List<AuditVO> selectAuditByApplicationId(Long id) {
        return auditMapper.selectAuditByApplicationId(id);
    }

    @Override
    public List<AuditVO> selectAudit(AuditQuery auditQuery) {
        log.debug("查询审批单列表业务,参数:{}", auditQuery);
        List<AuditVO> list = auditMapper.selectAudit(auditQuery);
        for(int i = 0; i < list.size(); i++){
            AuditVO auditVO = list.get(i);
            assignAuditUserList(auditVO);
        }
        return list;
    }

    @Override
    public void updateAudit(AuditSaveParam auditSaveParam) {
        log.debug("更新审批单业务,参数:{}", auditSaveParam);
        Audit audit = new Audit();
        BeanUtils.copyProperties(auditSaveParam, audit);
        audit.setUpdateTime(new Date());
        //先更新当前操作的这个审批人对应的这条审批单
        auditMapper.update(audit);

        //还要更新上面审批人批的这个申请单
        Application application = new Application();
        application.setId(audit.getApplicationId());
        application.setUpdateTime(new Date());
        /* 需要判断是通过操作还是驳回操作，分头处理 */
        if(audit.getAuditStatus().equals(AuditStatusEnum.AUDITED.getCode())){//通过
            //继续查是否还存在未审批的审批单
            AuditQuery auditQuery = new AuditQuery();
            auditQuery.setApplicationId(audit.getApplicationId());
            Integer count = auditMapper.selectRestAuditCount(auditQuery);
            if(count>0){//还有未审批的审批单
                //下一条审批单是上一条审批单（批同一个申请单&审批次序+1）
                auditQuery.setAuditSort(audit.getAuditSort()+1);
                List<AuditVO> auditVOList = auditMapper.selectAudit(auditQuery);
                if(auditVOList !=null && auditVOList.size()>0){
                    AuditVO auditVO = auditVOList.get(0);
                    //创建第2个空对象，代表第2条审批单数据
                    Audit audit2 = new Audit();
                    audit2.setId(auditVO.getId());
                    audit2.setAuditStatus(AuditStatusEnum.MY_PENDING.getCode());
                    audit2.setUpdateTime(new Date());
                    auditMapper.update(audit2);
                }
                //还需要将申请单改成 30 审核中
                application.setStatus(ApplicationStatusEnum.AUDIT.getCode());
                applicationMapper.update(application);
            }else{//没有未审批的审批单
                //所以：申请单要更新成 50 已审核
                application.setStatus(ApplicationStatusEnum.AUDITED.getCode());
                applicationMapper.update(application);
            }
        }else{//驳回
            AuditQuery auditQuery = new AuditQuery();
            auditQuery.setApplicationId(audit.getApplicationId());
            //查询批此申请单对应的所有审批单(申请单id就是上方更新的那条审批单批的申请单)
            List<AuditVO> auditVOList = auditMapper.selectAudit(auditQuery);
            if(auditVOList !=null && auditVOList.size()>0){
                //遍历所有的审批单
                for(int i = 0; i < auditVOList.size(); i++){
                    AuditVO auditVO = auditVOList.get(i);
                    //如果当前审批单的状态为“待他人审核”，也就是还未审核，删除
                    if(auditVO.getAuditStatus().equals(AuditStatusEnum.PENDING.getCode())){
                        auditMapper.deleteById(auditVO.getId());
                    }
                }
            }
            //设置申请单状态为“驳回”
            application.setStatus(ApplicationStatusEnum.REJECT.getCode());
            application.setRejectReason(auditSaveParam.getRejectReason());
            applicationMapper.update(application);
        }
    }

    private void assignAuditUserList(AuditVO auditVO) {
        List<String> auditUsernameList = new ArrayList<>();
        List<Long> auditUserIdList = new ArrayList<>();
        List<AuditVO> auditVOList =
                auditMapper.selectAuditByApplicationId(auditVO.getApplicationId());
        for(int i = 0; i < auditVOList.size(); i++){
            Long userId = auditVOList.get(i).getAuditUserId();
            auditUserIdList.add(userId);
            UserVO userVO = userMapper.selectById(userId);
            auditUsernameList.add(userVO.getUsername());
        }
        StringJoiner stringJoiner = new StringJoiner(",");
        for(String username : auditUsernameList){
            stringJoiner.add(username);
        }
        auditVO.setAuditUserIdList(auditUserIdList);
        auditVO.setAuditUsernameList(stringJoiner.toString());
    }
}
