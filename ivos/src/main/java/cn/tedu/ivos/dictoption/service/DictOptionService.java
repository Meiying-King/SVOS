package cn.tedu.ivos.dictoption.service;

import cn.tedu.ivos.dictoption.pojo.dto.DictOptionQuery;
import cn.tedu.ivos.dictoption.pojo.dto.DictOptionSaveParam;
import cn.tedu.ivos.dictoption.pojo.vo.DictOptionVO;

import java.util.List;

public interface DictOptionService {
    List<DictOptionVO> selectDictOption(DictOptionQuery dictOptionQuery);

    void saveDictOption(DictOptionSaveParam dictOptionSaveParam);

    void deleteDictOption(Long id);

    List<DictOptionVO> selectDictOptionByCode(String code);
}
