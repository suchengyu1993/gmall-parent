package com.suchengyu.gmall.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.suchengyu.gmall.list.service.ListApiService;
import com.suchengyu.gmall.model.list.*;
import com.suchengyu.gmall.model.product.BaseCategoryView;
import com.suchengyu.gmall.model.product.BaseTrademark;
import com.suchengyu.gmall.model.product.SkuInfo;
import com.suchengyu.gmall.product.controller.ProductFeignClient;
import jodd.util.StringUtil;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ListApiServiceImpl
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-28
 * @Description:
 */
@Service
public class ListApiServiceImpl implements ListApiService {

    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

//    public static void main(String[] args) {
//
//        //dsl语句
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        //分页
//        searchSourceBuilder.from(0);
//        searchSourceBuilder.size(20);
//        //query
//        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
//        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title", "移动");
//        boolQueryBuilder.must(matchQueryBuilder);
//        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("category3Id", "61");
//        boolQueryBuilder.must(termQueryBuilder);
//        searchSourceBuilder.query(boolQueryBuilder);
//        System.out.println("====" + searchSourceBuilder.toString() + "==========");
//        //请求命令对象的封装
//        String[] indexs = {"goods"};
//        SearchRequest searchRequest = new SearchRequest(indexs, searchSourceBuilder);
//    }


    /**
     * 列表搜索商品
     */
    public SearchResponseVo list(SearchParam searchParam) {
        //拼接dsl的封装
        SearchRequest searchRequest = buildQueryDsl(searchParam);
        //执行dsl语句
        SearchResponse search =null;
        try {
            search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //解析结果,封装vo对象
        SearchResponseVo searchResponseVo = parseSearchResult(search);
        //分页
        searchResponseVo.setPageSize(searchParam.getPageSize());//传入的每页数量
        searchResponseVo.setPageNo(searchParam.getPageNo());//传入的当前页
        Long totalPages =  (searchResponseVo.getTotal()+searchParam.getPageSize()-1)/searchParam.getPageSize();
        searchResponseVo.setTotalPages(totalPages);
        System.out.println("调用成功");
        return searchResponseVo;
    }

    /**
     *执行列表搜索的dsl语句的拼接
     */
    private SearchRequest buildQueryDsl(SearchParam searchParam) {
        String[] indexs = {"goods"};
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();//搜索源生成器
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();//总查询器
        //三级分类过滤
        Long category1Id = searchParam.getCategory1Id();
        Long category2Id = searchParam.getCategory2Id();
        Long category3Id = searchParam.getCategory3Id();
        if(null != category3Id && category3Id > 0){
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("category3Id", category3Id);//字符查询生成器
            boolQueryBuilder.filter(termQueryBuilder);//添加进总查询器
        }
        //关键字查询
        String keyword = searchParam.getKeyword();
        if (StringUtil.isNotBlank(keyword)){
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title", keyword);//匹配查询生成器
            boolQueryBuilder.must(matchQueryBuilder);
        }
        // 属性集合
        // 属性id:属性值名称:属性名称
        String[] props = searchParam.getProps();
        if (null != props && props.length > 0){
            for (String prop : props) {
                // 23:4G:运行内存
                String[] split = prop.split(":");//根据传入的字符串分割成数组
                String attrId = split[0];
                String attrValue = split[1];
                String attrName = split[2];
                // nested的属性
                BoolQueryBuilder attrBool = new BoolQueryBuilder();
                attrBool.filter(new TermQueryBuilder("attrs.attrId",attrId));
                attrBool.filter(new TermQueryBuilder("attrs.attrValue",attrValue));
                attrBool.must(new MatchQueryBuilder("attrs.attrName",attrName));
                NestedQueryBuilder nestedQueryBuilder = new NestedQueryBuilder("attrs",attrBool, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }
        }
        //商标查询
        String trademark = searchParam.getTrademark();
        if (StringUtil.isNotBlank(trademark)){
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("tmId", trademark.split(":")[0]);
            boolQueryBuilder.filter(termQueryBuilder);
        }
        //封装总查询条件
        searchSourceBuilder.query(boolQueryBuilder);
        //聚合商标结果
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("tmIdAgg").field("tmId")//创建聚合查询
                .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
                .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"));
        searchSourceBuilder.aggregation(termsAggregationBuilder);
        //聚合属性结果
        NestedAggregationBuilder attrNestedAggregationBuilder = AggregationBuilders.nested("attrAgg", "attrs")
                .subAggregation(
                        AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
                                .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"))
                                .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                );
        searchSourceBuilder.aggregation(attrNestedAggregationBuilder);
        //分页
        int from = (searchParam.getPageNo()-1)*searchParam.getPageSize();
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(searchParam.getPageSize());

        //高亮
        if (StringUtil.isNotBlank(keyword)){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<span style='color:red;font-weight:bolder'>");
            highlightBuilder.field("title");
            highlightBuilder.postTags("</span>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }
        //排序
        String order = searchParam.getOrder();
        if (StringUtil.isNotBlank(order)){
            String[] split = order.split(":");
            String fieldFlag = split[0];
            String sortOrder = split[1];
            String field = "hotScore";
            if (fieldFlag.equals("2")){
                field="price";
            }
            searchSourceBuilder.sort(field,sortOrder.equals("asc")? SortOrder.ASC:SortOrder.DESC);
        }
        //封装进查询请求
        SearchRequest searchRequest = new SearchRequest(indexs, searchSourceBuilder);
        System.out.println(searchSourceBuilder.toString());
        return searchRequest;
    }

    /**
     *解析搜索返回结果,封装vo对象
     */
    private SearchResponseVo parseSearchResult(SearchResponse search) {
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        //商品数据封装
        List<Goods> goods = new ArrayList<>();
        SearchHits hits = search.getHits();//获取命中
        SearchHit[] resultHits = hits.getHits();//获取命中数据
        if(null != resultHits && resultHits.length > 0){
            for (SearchHit resultHit : resultHits) {
                String sourceAsString = resultHit.getSourceAsString();
                Goods good = JSON.parseObject(sourceAsString, Goods.class);
                //解析高亮
                Map<String, HighlightField> highlightFields = resultHit.getHighlightFields();
                if (null != highlightFields && highlightFields.size() > 0){
                    HighlightField title = highlightFields.get("title");
                    String titleName = title.getFragments()[0].toString();
                    good.setTitle(titleName);
                }
                goods.add(good);
            }
        }
        // 商标聚合解析
        Map<String, Aggregation> stringAggregationMap = search.getAggregations().asMap();
        ParsedLongTerms tmIdAggParsedLongTerms = (ParsedLongTerms) stringAggregationMap.get("tmIdAgg");
        //使用流式编程
        List<SearchResponseTmVo> trademarkList = tmIdAggParsedLongTerms.getBuckets().stream().map(bucket->{
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            String tmId = ((Terms.Bucket) bucket).getKeyAsString();
            //跟解析tmId的聚合一样,再进行一次聚合,拿到tmName
            Map<String, Aggregation> tmIdSubMap = ((Terms.Bucket) bucket).getAggregations().asMap();
            ParsedStringTerms tmNameAgg = (ParsedStringTerms) tmIdSubMap.get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            //跟解析tmId的聚合一样,再进行一次聚合,拿到tmLogoUrl
            Map<String, Aggregation> tmLogoUrlSubMap = ((Terms.Bucket) bucket).getAggregations().asMap();
            ParsedStringTerms tmLogoAgg = (ParsedStringTerms) tmLogoUrlSubMap.get("tmLogoUrlAgg");
            String tmLogoUrl = tmLogoAgg.getBuckets().get(0).getKeyAsString();
            searchResponseTmVo.setTmId(Long.parseLong(tmId));
            searchResponseTmVo.setTmName(tmName);
            searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
            return searchResponseTmVo;
        }).collect(Collectors.toList());

        // 属性聚合解析
        ParsedNested attrsAgg = (ParsedNested) stringAggregationMap.get("attrAgg");
        ParsedLongTerms attrIdAgg = attrsAgg.getAggregations().get("attrIdAgg");
        List<? extends Terms.Bucket> attrIdBuckets = attrIdAgg.getBuckets();
        List<SearchResponseAttrVo> searchResponseAttrVos = attrIdBuckets.stream().map(attrIdBucket->{
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            //先获取聚合的Id值
            long attrId = ((Terms.Bucket) attrIdBucket).getKeyAsNumber().longValue();
            searchResponseAttrVo.setAttrId(attrId);
            //再获取聚合的attrName值
            Map<String, Aggregation> attrNameMap = ((Terms.Bucket) attrIdBucket).getAggregations().asMap();
            ParsedStringTerms attrNameAgg = (ParsedStringTerms) attrNameMap.get("attrNameAgg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            searchResponseAttrVo.setAttrName(attrName);
            //attrValue是一个list的集合,再聚合拿到
            Map<String, Aggregation> attrValueMap = ((Terms.Bucket) attrIdBucket).getAggregations().asMap();
            ParsedStringTerms attrValueAgg = (ParsedStringTerms) attrValueMap.get("attrValueAgg");
            List<String> attrValues = attrValueAgg.getBuckets().stream().map(attrValueBucket->{
                String attrValue = ((Terms.Bucket) attrValueBucket).getKeyAsString();
                return attrValue;
            }).collect(Collectors.toList());
            searchResponseAttrVo.setAttrValueList(attrValues);// 封装属性值的解析结果给属性Vo集合
            return searchResponseAttrVo;
        }).collect(Collectors.toList());

        searchResponseVo.setTotal(hits.totalHits);//获取总记录数
        searchResponseVo.setGoodsList(goods);
        searchResponseVo.setTrademarkList(trademarkList);
        searchResponseVo.setAttrsList(searchResponseAttrVos);
        return searchResponseVo;
    }


    //更新商品时,增加热度
    public void incrHotScore(Long skuId) {
        //使用Redis的ZSet可以设置热度
        Double hotKey = redisTemplate.opsForZSet().incrementScore("hotKey", "skuId:" + skuId, 1);
        if (hotKey%10 == 0){
            //更新数据到es中
            Optional<Goods> hotOptional = goodsRepository.findById(skuId);
            //获取goods
            Goods goods = hotOptional.get();
            goods.setHotScore(Math.round(hotKey));
            goodsRepository.save(goods);
        }
    }

    //商品上架,添加数据到es的index中
    public void upperGoods(Long skuId) {
        Goods goods = new Goods();
        //封装sku基本信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if (null != skuInfo){
            goods.setId(skuInfo.getId());
            goods.setDefaultImg(skuInfo.getSkuDefaultImg());
            goods.setTitle(skuInfo.getSkuName());
            goods.setPrice(skuInfo.getPrice().doubleValue());
        }
        //封装sku的分类信息
        BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
        if (null != categoryView){
            goods.setCategory1Id(categoryView.getCategory1Id());
            goods.setCategory1Name(categoryView.getCategory1Name());
            goods.setCategory2Id(categoryView.getCategory2Id());
            goods.setCategory2Name(categoryView.getCategory2Name());
            goods.setCategory3Id(categoryView.getCategory3Id());
            goods.setCategory3Name(categoryView.getCategory3Name());
        }
        //封装sku对应的平台属性信息
        List<SearchAttr> searchAttrList = productFeignClient.getAttrList(skuId);
        goods.setAttrs(searchAttrList);
        //封装品牌
        BaseTrademark baseTrademark = productFeignClient.getTrademark(skuInfo.getTmId());
        if(null != baseTrademark){
            goods.setTmId(skuInfo.getTmId());
            goods.setTmName(baseTrademark.getTmName());
            goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        }
        goods.setCreateTime(new Date());
        //往es中添加数据
        goodsRepository.save(goods);
    }

    //商品下架,删除es中的数据
    public void lowerGoods(Long skuId) {
        goodsRepository.deleteById(skuId);
    }
}
