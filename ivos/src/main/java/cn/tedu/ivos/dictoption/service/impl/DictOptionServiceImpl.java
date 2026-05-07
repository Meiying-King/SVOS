package cn.tedu.ivos.dictoption.service.impl;

import cn.tedu.ivos.base.exception.ServiceException;
import cn.tedu.ivos.base.response.StatusCode;
import cn.tedu.ivos.base.util.CacheUtils;
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

    @Autowired
    CacheUtils cacheUtils;

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
        // 新增或更新后，将dictOption的最新数据存入redis中
        DictQuery dictQuery = new DictQuery();
        dictQuery.setId(dictOption.getDictId());
        List<DictVO> dictVOList = dictMapper.selectDict(dictQuery);
        if(dictVOList!=null && dictVOList.size()>0){
            DictVO dictVO = dictVOList.get(0);
            //完整的redis key是 "dictOption"+dictVO.getCode()
            //dictOption是前缀,比如dictOptionvehicle_color
            //根据dict的code删除之前的redis缓存
            cacheUtils.delete("dictOption"+dictVO.getCode());
            //封装字典项查询dto
            DictOptionQuery dictOptionQuery = new DictOptionQuery();
            dictOptionQuery.setDictId(dictVO.getId());
            // 查询对应的字典项
            List<DictOptionVO> dictOptionVOList=dictOptionMapper.selectDictOption(dictOptionQuery);
            cacheUtils.set("dictOption"+dictVO.getCode(),dictOptionVOList);
        }

    }

    @Override
    public void deleteDictOption(Long id) {
        log.debug("删除字典项业务,参数:{}",id);
        //只删除了数据库中的数据,会造成数据库的数据与redis数据不一致  要同时把redis也进行更新
        DictOptionQuery optionQuery = new DictOptionQuery();
        optionQuery.setId(id);
        List<DictOptionVO> optionVOList = dictOptionMapper.selectDictOption(optionQuery);
        if(optionVOList!=null && optionVOList.size()>0){
            DictOptionVO dictOptionVO = optionVOList.get(0);
            DictQuery dictQuery = new DictQuery();
            dictQuery.setId(dictOptionVO.getDictId());
            List<DictVO> dictVOList = dictMapper.selectDict(dictQuery);
            if(dictVOList!=null && dictVOList.size()>0){
                DictVO dictVO = dictVOList.get(0);
                //根据dict的code删除之前的redis缓存
                cacheUtils.delete("dictOption"+dictVO.getCode());
                //封装字典项查询Dto
                DictOptionQuery dictOptionQuery = new DictOptionQuery();
                dictOptionQuery.setDictId(dictVO.getId());
                List<DictOptionVO> dictOptionVOList = dictOptionMapper.selectDictOption(dictOptionQuery);
                // 设置的redis缓存
                cacheUtils.set("dictOption"+dictVO.getCode(),dictOptionVOList);
            }
        }
        dictOptionMapper.deleteById(id);
    }

    @Override
    public List<DictOptionVO> selectDictOptionByCode(String code) {
        log.debug("根据字典code查其对应所有字典项,参数:{}",code);
        Object object = cacheUtils.get("dictOption"+code);
        if (object!=null){
            //缓存中有就直接返回缓存中的数据,避免再次查询数据库
            log.debug("从缓存中获取字典项,查询的字典项={}",code);
            //强制类型转换因为本方法的返回值类型就是List<DictOptionVO>
            //在这里说明从redis查到了数据,就直接把从redis查到的数据返回
            //不需要再往下处理查询mysql数据库了
            return (List<DictOptionVO>) object;
        }
        log.debug("从缓存中没有获取到字典项,查询的字典项={}",code);
        DictQuery dictQuery = new DictQuery();//封装字典查询对象
        dictQuery.setCode(code);//设置字典编码
        List<DictVO> dictVOList = dictMapper.selectDict(dictQuery);//根据查询对象查字典
        if(dictVOList != null && dictVOList.size() > 0){//如果查到了字典
            DictVO dictVO = dictVOList.get(0);//获取字典对象
            DictOptionQuery dictOptionQuery = new DictOptionQuery();//封装字典项查询对象
            dictOptionQuery.setDictId(dictVO.getId());//设置字典id
            List<DictOptionVO> dictOptionVOList =
                    dictOptionMapper.selectDictOption(dictOptionQuery);//根据查询对象查字典项
            log.debug("从数据库中获取到字典项,查询的字典项={}",code);
            cacheUtils.set("dictOption"+code,dictOptionVOList);
            log.debug("将字典项设置到缓存,查询的字典项={}",code);
            return dictOptionVOList;//返回字典项列表
        }else{
            throw new ServiceException(StatusCode.DATA_UNEXISTS);//如果根据code查不到字典,返回数据不存在
        }
    }
}
