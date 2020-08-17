package com.suchengyu.gmall.all.controller;

import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.list.client.ListFeignClient;
import com.suchengyu.gmall.model.list.SearchAttr;
import com.suchengyu.gmall.model.list.SearchParam;
import com.suchengyu.gmall.product.controller.ProductFeignClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ListController
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-30
 * @Description:产品列表接口
 */
@Api(description = "前台产品列表接口")
@Controller
public class ListController {

    @Autowired
    private ListFeignClient feignClient;
    @Autowired
    private ProductFeignClient productFeignClient;

    // TODO
    @GetMapping({"/","index.html"})
    public String index(HttpServletRequest request,SearchParam searchParam,Model model){
        String userId = request.getHeader("userId");
        Result result = productFeignClient.getBaseCategoryList();
        model.addAttribute("list", result.getData());
        return "index";
    }

    @ApiOperation(value = "列表搜索商品")
    @RequestMapping({"/list.html","/search.html"})
    public String list(SearchParam searchParam, Model model){
        Result<Map> result = feignClient.list(searchParam);
        model.addAllAttributes(result.getData());
        //处理排序
        if(StringUtils.isNotBlank(searchParam.getOrder())){
            String[] split = searchParam.getOrder().split(":");
            String fieldFlag = split[0];
            String sortOrder = split[1];
            HashMap<String, String> OrderMap = new HashMap<>();
            OrderMap.put("sort", sortOrder);
            OrderMap.put("type", fieldFlag);
            model.addAttribute("orderMap", OrderMap);
        }

        //面包屑
        if (StringUtils.isNotBlank(searchParam.getTrademark())){
            model.addAttribute("trademarkParam", searchParam.getTrademark().split(":")[1]);
        }
        String[] props = searchParam.getProps();
        if (null != props && props.length>0){
           List<SearchAttr> searchAttrs = new ArrayList<>();
           //循环props参数封装面包屑
            for (String prop : props) {
                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrId(Long.parseLong(prop.split(":")[0]));
                searchAttr.setAttrValue(prop.split(":")[1]);
                searchAttr.setAttrName(prop.split(":")[2]);
                searchAttrs.add(searchAttr);
            }
            model.addAttribute("propsParamList", searchAttrs);
        }

        //记录拼接
        String urlParam = makeUrlParam(searchParam);
        //处理品牌条件回显
        String trademarkParam = this.makeTrademark(searchParam.getTrademark());
        //处理平台属性条件回显
        List<Map<String, String>> propsParamList = this.makePropos(searchParam.getProps());
        model.addAttribute("trademarkParam", trademarkParam);
        model.addAttribute("propsParamList", propsParamList);
        model.addAttribute("searchParam", searchParam);
        model.addAttribute("urlParam", urlParam);
        return "list/index";
    }

    //制作返回的url
    private String makeUrlParam(SearchParam searchParam){
        StringBuilder urlParam = new StringBuilder();
        //判断关键字
        if (searchParam.getKeyword()!=null){
            urlParam.append("keyword").append(searchParam.getKeyword());
        }
        //判断一级分类
        if(searchParam.getCategory1Id()!=null){
            urlParam.append("category1Id").append(searchParam.getCategory1Id());
        }
        //判断二级分类
        if(searchParam.getCategory2Id()!=null){
            urlParam.append("category2Id").append(searchParam.getCategory2Id());
        }
        //判断三级分类
        if(searchParam.getCategory3Id()!=null){
            urlParam.append("category3Id").append(searchParam.getCategory3Id());
        }
        //处理品牌
        if (searchParam.getTrademark()!=null){
                urlParam.append("&trademark=").append(searchParam.getTrademark());
        }
        //判断平台属性值
        if (null != searchParam.getProps()){
            for (String prop : searchParam.getProps()) {
                    urlParam.append("&props=").append(prop);
            }
        }
        return "list.html?" + urlParam.toString();
    }

    /**
     *处理品牌条件回显
     */
    private String makeTrademark(String trademark){
        if (!StringUtils.isEmpty(trademark)){
            String[] split = StringUtils.split(trademark, ":");
            if (split == null || split.length ==2){
                return "品牌" + split[1];
            }
        }
        return "";
    }

    /**
     * 处理平台属性条件回显
     */
    private List<Map<String,String>> makePropos(String[] props){
        List<Map<String,String>> list = new ArrayList<>();
        //2:v:m
        if (props != null && props.length != 0){
            for (String prop : props) {
                String[] split = StringUtils.split(prop, ":");
                if (null != split && split.length ==3){
                    Map<String, String> map = new HashMap<>();
                    map.put("attrId", split[0]);
                    map.put("attrValue", split[1]);
                    map.put("attrName", split[2]);
                    list.add(map);
                }
            }
        }
        return list;
    }


}
