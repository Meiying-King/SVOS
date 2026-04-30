package cn.tedu.ivos.user.service.impl;

import cn.tedu.ivos.base.exception.ServiceException;
import cn.tedu.ivos.base.response.StatusCode;
import cn.tedu.ivos.base.util.PasswordUtil;
import cn.tedu.ivos.user.mapper.UserMapper;
import cn.tedu.ivos.user.pojo.dto.UserLoginParam;
import cn.tedu.ivos.user.pojo.dto.UserQuery;
import cn.tedu.ivos.user.pojo.dto.UserSaveParam;
import cn.tedu.ivos.user.pojo.entity.User;
import cn.tedu.ivos.user.pojo.vo.UserVO;
import cn.tedu.ivos.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public UserVO login(UserLoginParam userLoginParam) {
        log.debug("用户登录业务,参数:{}",userLoginParam);
        UserVO userVO = userMapper.selectByUsername(userLoginParam.getUsername());
        //一旦抛出异常,就会被全局异常处理器拦截到,并由全局异常处理器给前端响应结果!
        if(userVO == null){
           throw new ServiceException(StatusCode.USERNAME_ERROR);
        }
        if(!userVO.getPassword().equals(userLoginParam.getPassword())){
            throw new ServiceException(StatusCode.PASSWORD_ERROR);
        }
        //只有遇不到上面两个异常,才会走下面,由Controller层给前端返回结果!
        log.debug("用户登录业务,查询结果:{}",userVO);
        return userVO;
    }

    @Override
    public List<UserVO> selectUser(UserQuery userQuery) {
        log.debug("查询用户业务,参数:{}",userQuery);
        List<UserVO> list = userMapper.selectUser(userQuery);
        return list;
    }

    @Override
    public void save(UserSaveParam userSaveParam) {
        log.debug("保存用户业务,参数:{}",userSaveParam);
        User user = new User();
        BeanUtils.copyProperties(userSaveParam,user);
        if(user.getId() == null){//新增
            user.setPassword("123456");
            user.setCreateTime(new Date());
            userMapper.insert(user);
        }else{//更新
            user.setUpdateTime(new Date());
            userMapper.update(user);
        }

    }

    @Override
    public void updateStatus(Long userId, String status) {
        log.debug("修改用户状态业务,参数:{},{}",userId,status);
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        user.setUpdateTime(new Date());
        userMapper.update(user);
    }

    @Override
    public void deleteUser(Long userId) {
        log.debug("删除用户业务:userId={}",userId);
        userMapper.deleteById(userId);
    }

    @Override
    public List<UserVO> selectAudit(Long parentId) {
        log.debug("查询审批人列表业务:parentId={}",parentId);
        //1.先准备一个空集合,用来分次装入不同的审批人
        ArrayList<UserVO> userVOList = new ArrayList<>();
        //2.先根据参数,将直属领导查出来
        UserVO auditUser1 = userMapper.selectById(parentId);
        userVOList.add(auditUser1);
        //3.如果查出来了直属领导,且直属领导还有领导,继续查
        if(auditUser1 !=null && auditUser1.getParentId() != null){
            UserVO auditUser2 = userMapper.selectById(auditUser1.getParentId());
            userVOList.add(auditUser2);
        }
        //4.返回结果
        return userVOList;
    }

    @Override
    public void updatePassword(Long userId) {
        log.debug("修改密码业务:userId={}",userId);
        User user = new User();
        user.setId(userId);
        //user.setPassword("root");
        user.setPassword(PasswordUtil.generateRandomPassword(7));
        user.setUpdateTime(new Date());
        userMapper.update(user);
    }
}
