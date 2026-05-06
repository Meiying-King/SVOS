package cn.tedu.ivos.dict.service;

import cn.tedu.ivos.base.response.PageData;
import cn.tedu.ivos.dict.pojo.dto.DictQuery;
import cn.tedu.ivos.dict.pojo.dto.DictSaveParam;
import cn.tedu.ivos.dict.pojo.vo.DictVO;

import java.util.List;

public interface DictService {
//    List<DictVO> selectDict(DictQuery dictQuery);

    void saveDict(DictSaveParam dictSaveParam);

    void deleteDict(Long id);

    PageData selectDict(DictQuery dictQuery);
}
