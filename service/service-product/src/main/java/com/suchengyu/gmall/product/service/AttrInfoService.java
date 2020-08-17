package com.suchengyu.gmall.product.service;

import com.suchengyu.gmall.model.list.SearchAttr;
import com.suchengyu.gmall.model.product.BaseAttrInfo;
import com.suchengyu.gmall.model.product.BaseAttrValue;

import java.util.List;

/**
 * AttrInfoService
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-14
 * @Description:
 */
public interface AttrInfoService {

    //根据分类id获取平台属性
    List<BaseAttrInfo> attrInfoList(String category1Id, String category2Id, String category3Id);

    //添加或修改平台属性与平台属性值
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    //根据平台属性ID获取平台属性值
    List<BaseAttrValue> getAttrValueList(Long attrId);

    //查询sku对应的平台属性信息
    List<SearchAttr> getAttrList(Long skuId);
}
