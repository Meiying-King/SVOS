package cn.tedu.ivos.dict.service.impl;

import cn.tedu.ivos.base.response.PageData;
import cn.tedu.ivos.base.util.CacheUtils;
import cn.tedu.ivos.dict.mapper.DictMapper;
import cn.tedu.ivos.dict.pojo.dto.DictQuery;
import cn.tedu.ivos.dict.pojo.dto.DictSaveParam;
import cn.tedu.ivos.dict.pojo.entity.Dict;
import cn.tedu.ivos.dict.pojo.vo.DictVO;
import cn.tedu.ivos.dict.service.DictService;
import cn.tedu.ivos.dictoption.mapper.DictOptionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class DictServiceImpl implements DictService {
    @Autowired
    DictMapper dictMapper;

    @Autowired
    CacheUtils cacheUtils;

//    @Override
//    public List<DictVO> selectDict(DictQuery dictQuery) {
//        log.debug("查询字典业务,参数:{}",dictQuery);
//        List<DictVO> list = dictMapper.selectDict(dictQuery);
//        return list;
//    }

    @Override
    public void saveDict(DictSaveParam dictSaveParam) {
        log.debug("保存字典业务,参数:{}",dictSaveParam);
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictSaveParam,dict);
        if(dict.getId()==null){//新增
            dict.setStatus("1");//默认启用状态
            dict.setCreateTime(new Date());
            dictMapper.insert(dict);
        }else{//更新
            dict.setUpdateTime(new Date());
            dictMapper.update(dict);
        }
    }

    @Override
    public void deleteDict(Long id) {
        log.debug("删除字典业务,参数:{}",id);
        Dict dict = new Dict();
        dict.setId(id);
        dict.setStatus("0");
        dict.setUpdateTime(new Date());

        DictQuery dictQuery = new DictQuery();
        dictQuery.setId(id);
        List<DictVO> dictVOList = dictMapper.selectDict(dictQuery);
        if(dictVOList!=null && dictVOList.size()>0){
            DictVO dictVO = dictVOList.get(0);
            //根据dict的code删除之前的redis缓存
            cacheUtils.delete("dictOption"+dictVO.getCode());
        }
        dictMapper.update(dict);

    }

    @Override
    public PageData selectDict(DictQuery dictQuery) {
        //根据条件查询当前页DictVO集合
        List<DictVO> list = dictMapper.selectDict(dictQuery);
        PageData pageData = new PageData();
        pageData.setList(list);
        pageData.setTotal(dictMapper.selectDictCount(dictQuery));
        return pageData;
    }
}
