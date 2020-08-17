package com.suchengyu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;
import com.suchengyu.gmall.model.product.BaseCategory1;
import com.suchengyu.gmall.model.product.BaseCategory2;
import com.suchengyu.gmall.model.product.BaseCategory3;
import com.suchengyu.gmall.model.product.BaseCategoryView;

import java.util.List;

/**
 * CategoryService
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-14
 * @Description:
 */
public interface CategoryService {

    //查询所有一级分类信息
    List<BaseCategory1> getCategory1();

    //根据一级分类id查询所有二级分类信息
    List<BaseCategory2> getCategory2(String category1Id);

    //根据二级分类id查询所有三级分类信息
    List<BaseCategory3> getCategory3(String category2Id);

    //根据三级分类id查出分类信息
    BaseCategoryView getCategoryView(Long category3Id);

    //访问首页
    List<JSONObject> getBaseCategoryList();

}
