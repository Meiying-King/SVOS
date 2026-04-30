package cn.tedu.ivos.user.mapper;

import cn.tedu.ivos.user.pojo.dto.UserQuery;
import cn.tedu.ivos.user.pojo.entity.User;
import cn.tedu.ivos.user.pojo.vo.UserVO;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Repository;

import java.util.List;

/* 这个注解表明当前是数据访问层的组件,用于数据访问层的相关操作
* 可以让spring框架自动管理此类对象,还能进行事务管理等服务 */
@Repository
public interface UserMapper {
    UserVO selectByUsername(String username);

    List<UserVO> selectUser(UserQuery userQuery);

    void insert(User user);

    void update(User user);

    void deleteById(Long userId);

    UserVO selectById(Long parentId);
}
