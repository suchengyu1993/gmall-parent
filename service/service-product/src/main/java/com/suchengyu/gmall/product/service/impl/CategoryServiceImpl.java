package com.suchengyu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suchengyu.gmall.common.cache.GmallCache;
import com.suchengyu.gmall.model.product.BaseCategory1;
import com.suchengyu.gmall.model.product.BaseCategory2;
import com.suchengyu.gmall.model.product.BaseCategory3;
import com.suchengyu.gmall.model.product.BaseCategoryView;
import com.suchengyu.gmall.product.mapper.BaseCategory1Mapper;
import com.suchengyu.gmall.product.mapper.BaseCategory2Mapper;
import com.suchengyu.gmall.product.mapper.BaseCategory3Mapper;
import com.suchengyu.gmall.product.mapper.BaseCategoryViewMapper;
import com.suchengyu.gmall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ManageServiceImpl
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-14
 * @Description:
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;
    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;
    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;
    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

    //访问首页 TODO
    public List<JSONObject> getBaseCategoryList() {
        return null;
    }

    //根据三级分类id查出分类信息
    @GmallCache(prefix = "category:",suffix = ":view")
    public BaseCategoryView getCategoryView(Long category3Id) {
        QueryWrapper<BaseCategoryView> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id",category3Id);
        BaseCategoryView baseCategoryView = baseCategoryViewMapper.selectOne(wrapper);
        return baseCategoryView;
    }

    //查询所有一级分类信息
    public List<BaseCategory1> getCategory1() {
        List<BaseCategory1> list = baseCategory1Mapper.selectList(null);
        return list;
    }

    //根据一级分类id查询所有二级分类信息
    public List<BaseCategory2> getCategory2(String category1Id) {
        QueryWrapper<BaseCategory2> wrapper = new QueryWrapper<>();
        wrapper.eq("category1_id",category1Id);
        List<BaseCategory2> baseCategory2s = baseCategory2Mapper.selectList(wrapper);
        return baseCategory2s;
    }

    //根据二级分类id查询所有三级分类信息
    public List<BaseCategory3> getCategory3(String category2Id) {
        QueryWrapper<BaseCategory3> wrapper = new QueryWrapper<>();
        wrapper.eq("category2_id",category2Id);
        List<BaseCategory3> baseCategory3s = baseCategory3Mapper.selectList(wrapper);
        return baseCategory3s;
    }

}
