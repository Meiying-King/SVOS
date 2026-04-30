package cn.tedu.ivos.application.mapper;

import cn.tedu.ivos.application.pojo.dto.ApplicationQuery;
import cn.tedu.ivos.application.pojo.entity.Application;
import cn.tedu.ivos.application.pojo.vo.ApplicationVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationMapper {
    void insert(Application application);

    List<ApplicationVO> selectApplication(ApplicationQuery applicationQuery);

    void update(Application application);

    void back(Application application);
}
