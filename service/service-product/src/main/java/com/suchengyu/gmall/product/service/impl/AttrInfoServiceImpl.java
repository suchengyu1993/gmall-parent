package com.suchengyu.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suchengyu.gmall.model.list.SearchAttr;
import com.suchengyu.gmall.model.product.BaseAttrInfo;
import com.suchengyu.gmall.model.product.BaseAttrValue;
import com.suchengyu.gmall.product.mapper.BaseAttrInfoMapper;
import com.suchengyu.gmall.product.mapper.BaseAttrValueMapper;
import com.suchengyu.gmall.product.service.AttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AttrInfoServiceImpl
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-14
 * @Description:
 */
@Service
public class AttrInfoServiceImpl implements AttrInfoService {

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;


    //查询sku对应的平台属性信息
    public List<SearchAttr> getAttrList(Long skuId) {
        List<SearchAttr> searchAttrList = baseAttrInfoMapper.selectBaseAttrInfoListBySkuId(skuId);
        return searchAttrList;
    }

    //根据分类id获取平台属性
    public List<BaseAttrInfo> attrInfoList(String category1Id, String category2Id, String category3Id) {
        QueryWrapper<BaseAttrInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("category_level", 3);
        wrapper.eq("category_id", category3Id);
        List<BaseAttrInfo> list = baseAttrInfoMapper.selectList(wrapper);
        //还要把平台属性值也查出来
        for (BaseAttrInfo baseAttrInfo : list) {
            QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("attr_id", baseAttrInfo.getId());
            List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.selectList(queryWrapper);
            baseAttrInfo.setAttrValueList(baseAttrValueList);
        }
        return list;
    }

    //添加或修改平台属性与平台属性值
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        if(baseAttrInfo == null){
            return;
        }
        // 1. 获取id,判断是添加还是修改
        Long attrId = baseAttrInfo.getId();
        int updateResult = 0;
        int insertResult = 0;

        if(null != attrId && attrId > 0){
            // 2. id有值,执行修改操作
            updateResult= baseAttrInfoMapper.updateById(baseAttrInfo);
            //2.2 同时删除id下的平台属性值
            QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_id", attrId);
            baseAttrValueMapper.delete(wrapper);
        }else {
            // 3. id无值,执行添加操作,添加平台属性
             insertResult = baseAttrInfoMapper.insert(baseAttrInfo);
            attrId = baseAttrInfo.getId();
        }

        // 4. 上面成功都要添加平台属性值
        if (updateResult > 0 || insertResult > 0){
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            for (BaseAttrValue baseAttrValue : attrValueList) {
                // 设置平台属性id
                baseAttrValue.setAttrId(attrId);
                baseAttrValueMapper.insert(baseAttrValue);
            }
        }
    }

    //根据平台属性ID获取平台属性值
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        if (null != attrId && attrId > 0){
            QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_id", attrId);
            List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.selectList(wrapper);
            return baseAttrValueList;
        }
        return null;
    }

}
