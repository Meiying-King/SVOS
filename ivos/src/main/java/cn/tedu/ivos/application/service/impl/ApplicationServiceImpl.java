package cn.tedu.ivos.application.service.impl;

import cn.tedu.ivos.application.mapper.ApplicationMapper;
import cn.tedu.ivos.application.pojo.dto.ApplicationQuery;
import cn.tedu.ivos.application.pojo.dto.ApplicationSaveParam;
import cn.tedu.ivos.application.pojo.entity.Application;
import cn.tedu.ivos.application.pojo.vo.ApplicationVO;
import cn.tedu.ivos.application.service.ApplicationService;
import cn.tedu.ivos.audit.mapper.AuditMapper;
import cn.tedu.ivos.audit.pojo.vo.AuditVO;
import cn.tedu.ivos.audit.service.AuditService;
import cn.tedu.ivos.base.enums.ApplicationStatusEnum;
import cn.tedu.ivos.user.mapper.UserMapper;
import cn.tedu.ivos.vehicle.mapper.VehicleMapper;
import cn.tedu.ivos.vehicle.pojo.entity.Vehicle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

@Transactional
@Slf4j
@Service
public class ApplicationServiceImpl implements ApplicationService {
    @Autowired
    ApplicationMapper applicationMapper;
    @Autowired
    AuditService auditService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    AuditMapper auditMapper;
    @Autowired
    VehicleMapper vehicleMapper;


    @Override
    public void save(ApplicationSaveParam applicationSaveParam) {
        log.debug("新增申请单业务,参数:{}",applicationSaveParam);
        Application application = new Application();
        BeanUtils.copyProperties(applicationSaveParam,application);
        // 设置申请单状态为"已发起"(10)
        application.setStatus(ApplicationStatusEnum.PENDING.getCode());
        application.setCreateTime(new Date());
        /* 遇到的问题:新增申请单对应的审批单时,审批单数据没有此申请单id
        原因:执行insert方法的SQL时,并没有把刚刚生成的申请单id回填到application对象中
        解决办法:给此SQL上加useGeneratedKeys="true" keyProperty="id"属性
        效果:JDBC自动回填此申请单id到application对象的id属性中,再传给审批,审批就拿到申请单id了
        */
        applicationMapper.insert(application);

        //还需要为当前申请生成对应的1到多条审批单数据
        auditService.insertAudit(application);
    }

    @Override
    public List<ApplicationVO> selectApplication(ApplicationQuery applicationQuery) {
        log.debug("查询申请单业务,参数:{}",applicationQuery);
        List<ApplicationVO> list = applicationMapper.selectApplication(applicationQuery);
        //遍历得到每一个申请单VO,为它补全审批人相关的数据
        //List<Long> auditUserIdList;//审批人id集合 [106,103]
        //String auditUsernameList;//审批人姓名字符串 "moly,tom"
        for(int i = 0; i < list.size(); i++){
            ApplicationVO applicationVO = list.get(i);
            //创建一个自定义方法,为当前循环到的这个申请单VO补全审批人数据
            assignAuditUserList(applicationVO);
        }
        return list;
    }

    @Override
    public void cancel(Long id) {
        log.debug("撤销申请业务,参数:{}",id);
        Application application = new Application();
        application.setId(id);
        application.setStatus(ApplicationStatusEnum.CANCEL.getCode());
        application.setUpdateTime(new Date());
        applicationMapper.update(application);

        //还要删除此申请单对应的所有审批单数据
        auditMapper.deleteByApplicationId(id);
    }

    @Override
    public void distribute(Long applicationId, Long vehicleId) {
        log.debug("分配车辆业务,参数:{},{}",applicationId,vehicleId);
        Application application = new Application();
        application.setId(applicationId);
        application.setVehicleId(vehicleId);
        application.setStatus(ApplicationStatusEnum.ALLOCATION.getCode());
        application.setUpdateTime(new Date());
        applicationMapper.update(application);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setStatus("2");
        vehicle.setUpdateTime(new Date());
        vehicleMapper.update(vehicle);



    }

    @Override
    public void back(Long applicationId, Long vehicleId) {
        log.debug("还车业务,参数:{},{}",applicationId,vehicleId);
        Application application = new Application();
        application.setId(applicationId);
        application.setVehicleId(null);
        application.setStatus(ApplicationStatusEnum.END.getCode());
        application.setUpdateTime(new Date());
        applicationMapper.back(application);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setStatus("1");
        vehicle.setUpdateTime(new Date());
        vehicleMapper.update(vehicle);
    }

    private void assignAuditUserList(ApplicationVO applicationVO) {
        //1.准备空集合用来存放多个审批人id与姓名
        List<Long> auditUserIdList = new ArrayList<>();
        List<String> auditUsernameList = new ArrayList<>();
        //2.根据申请单id,查出批此申请单的所有审批单
        List<AuditVO> auditVOList = auditService.selectAuditByApplicationId(applicationVO.getId());
        //3.遍历依次取出每一个审批单VO
        for (AuditVO auditVO : auditVOList) {
            //4.获取当前审批单中的审批人id并存入上方的空集合中
            Long id = auditVO.getAuditUserId();
            auditUserIdList.add(id);
            //5.进一步根据用户id,查出用户姓名并添加到上边的空集合中
            String username = userMapper.selectById(id).getUsername();
            auditUsernameList.add(username);
        }
        //6.准备一个字符串拼接工具，帮我们把多个审批人姓名连成一个字符串
        StringJoiner stringJoiner = new StringJoiner(",");
        //7.将所有的名字依次添加到字符串拼接工具中
        for (String username : auditUsernameList) {
            stringJoiner.add(username);
        }
        //8.给传入的申请单VO补全数据
        applicationVO.setAuditUserIdList(auditUserIdList);
        applicationVO.setAuditUsernameList(stringJoiner.toString());
    }
}
