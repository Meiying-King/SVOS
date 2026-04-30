package cn.tedu.ivos.dict.mapper;

import cn.tedu.ivos.dict.pojo.dto.DictQuery;
import cn.tedu.ivos.dict.pojo.entity.Dict;
import cn.tedu.ivos.dict.pojo.vo.DictVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictMapper {
    List<DictVO> selectDict(DictQuery dictQuery);

    void insert(Dict dict);

    void update(Dict dict);
}
