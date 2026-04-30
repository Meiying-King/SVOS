package cn.tedu.ivos.dictoption.service.impl;

import cn.tedu.ivos.base.exception.ServiceException;
import cn.tedu.ivos.base.response.StatusCode;
import cn.tedu.ivos.dict.mapper.DictMapper;
import cn.tedu.ivos.dict.pojo.dto.DictQuery;
import cn.tedu.ivos.dict.pojo.vo.DictVO;
import cn.tedu.ivos.dictoption.mapper.DictOptionMapper;
import cn.tedu.ivos.dictoption.pojo.dto.DictOptionQuery;
import cn.tedu.ivos.dictoption.pojo.dto.DictOptionSaveParam;
import cn.tedu.ivos.dictoption.pojo.entity.DictOption;
import cn.tedu.ivos.dictoption.pojo.vo.DictOptionVO;
import cn.tedu.ivos.dictoption.service.DictOptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class DictOptionServiceImpl implements DictOptionService {
    @Autowired
    DictOptionMapper dictOptionMapper;
    @Autowired
    DictMapper dictMapper;

    @Override
    public List<DictOptionVO> selectDictOption(DictOptionQuery dictOptionQuery) {
        log.debug("查询字典项业务,参数:{}",dictOptionQuery);
        List<DictOptionVO> list = dictOptionMapper.selectDictOption(dictOptionQuery);
        return list;
    }

    @Override
    public void saveDictOption(DictOptionSaveParam dictOptionSaveParam) {
        log.debug("保存字典项业务,参数:{}",dictOptionSaveParam);
        DictOption dictOption = new DictOption();
        BeanUtils.copyProperties(dictOptionSaveParam,dictOption);
        if(dictOption.getId()==null){//新增
            dictOption.setCreateTime(new Date());
            dictOptionMapper.insert(dictOption);
        }else{//编辑
            dictOption.setUpdateTime(new Date());
            dictOptionMapper.update(dictOption);
        }
    }

    @Override
    public void deleteDictOption(Long id) {
        log.debug("删除字典项业务,参数:{}",id);
        dictOptionMapper.deleteById(id);
    }

    @Override
    public List<DictOptionVO> selectDictOptionByCode(String code) {
        log.debug("根据字典code查其对应所有字典项,参数:{}",code);
        DictQuery dictQuery = new DictQuery();//封装字典查询对象
        dictQuery.setCode(code);//设置字典编码
        List<DictVO> dictVOList = dictMapper.selectDict(dictQuery);//根据查询对象查字典
        if(dictVOList != null && dictVOList.size() > 0){//如果查到了字典
            DictVO dictVO = dictVOList.get(0);//获取字典对象
            DictOptionQuery dictOptionQuery = new DictOptionQuery();//封装字典项查询对象
            dictOptionQuery.setDictId(dictVO.getId());//设置字典id
            List<DictOptionVO> dictOptionVOList =
                    dictOptionMapper.selectDictOption(dictOptionQuery);//根据查询对象查字典项
            return dictOptionVOList;//返回字典项列表
        }else{
            throw new ServiceException(StatusCode.DATA_UNEXISTS);//如果根据code查不到字典,返回数据不存在
        }
    }
}
