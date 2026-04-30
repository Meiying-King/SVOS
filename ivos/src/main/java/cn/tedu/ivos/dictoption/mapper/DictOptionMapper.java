package cn.tedu.ivos.dictoption.mapper;

import cn.tedu.ivos.dictoption.pojo.dto.DictOptionQuery;
import cn.tedu.ivos.dictoption.pojo.entity.DictOption;
import cn.tedu.ivos.dictoption.pojo.vo.DictOptionVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictOptionMapper {
    List<DictOptionVO> selectDictOption(DictOptionQuery dictOptionQuery);

    void insert(DictOption dictOption);

    void update(DictOption dictOption);

    void deleteById(Long id);
}
