package com.suchengyu.gmall.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.common.result.ResultCodeEnum;
import com.suchengyu.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * AuthGlobalFilter
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-03
 * @Description:
 */
@Component
public class AuthGlobalFilter implements GlobalFilter {

   AntPathMatcher antPathMatcher =  new AntPathMatcher();
   @Autowired
   private UserFeignClient userFeignClient;
   @Value("${authUrls.url}")
   String authUrls;

    /**
     * Gateway网关拦截器,拦截所有的请求
     */
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //拿到请求的request和response
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //拿到当前请求的url
        URI uri = request.getURI();
        String path = request.getURI().getPath();
        //不拦截的请求,如cs,js,图片等
        if(path.lastIndexOf("login")!=-1||path.lastIndexOf(".png")!= -1 || path.lastIndexOf(".jpg") != -1
                || path.lastIndexOf(".css") != -1 || path.lastIndexOf(".js") != -1){
            return chain.filter(exchange);//放行
        }
        // 校验内部服务调用的url:api,则网关拦截不允许外部访问！
        if (antPathMatcher.match("/**/inner/**", path)){
            return out(response, ResultCodeEnum.PERMISSION);//没有权限,响应回信息
        }

        //获取用户id
        String userId = getUserId(request);
        boolean match = antPathMatcher.match("/**/auth/**", path);
        if (match){
            //用户未登录
            if (StringUtils.isEmpty(userId)){
                //跳转到登录页面
                response.setStatusCode(HttpStatus.SEE_OTHER);//响应码
                response.getHeaders().set(HttpHeaders.LOCATION,"http://www.gmall.com/login.html?originUrl=" + request.getURI());
                Mono<Void> voidMono = response.setComplete();
                return voidMono;
            }
        }

        //校验白名单(web系统请求)(订单模块,支付模块等)
        String requestUrl = request.getURI().toString();
        String[] split = authUrls.split(",");//获取配置文件中的白名单
        for (String url : split) {
            //访问的路径是需登录的模块,userId没值,返回登录页面
            if (requestUrl.indexOf(url) != -1 && StringUtils.isEmpty(userId)){
                //跳转到登录页面
                response.setStatusCode(HttpStatus.SEE_OTHER);//响应码
                response.getHeaders().set(HttpHeaders.LOCATION,"http://www.gmall.com/login.html?originUrl=" + request.getURI());
                Mono<Void> voidMono = response.setComplete();
                return voidMono;
            }
        }
        //设置网关请求头
        String userTempId = this.getUserTempId(request);
        if (!StringUtils.isEmpty(userId )|| !StringUtils.isEmpty(userTempId)){
            // 如果用户已经正常通过token的认证校验
            if(!StringUtils.isEmpty(userId)){
                request.mutate().header("userId", userId).build();//把userId放入请求头里
            }
            //如果用户未登录,使用临时ld
            if (!StringUtils.isEmpty(userTempId)){
                request.mutate().header("userTempId", userTempId).build();//把userTempId放入请求头里
            }
            //将现在的request 变成 exchange对象
            chain.filter(exchange.mutate().request(request).build());//放行,
        }

        return chain.filter(exchange);
    }

    /**
     *从请求的cookie中获取临时userTempId
     */
    private String getUserTempId(ServerHttpRequest request){
        String userTempId = "";
        //从请求头中获取userTempId
        List<String> tokenList = request.getHeaders().get("userTempId");
        if (null != tokenList && tokenList.size() > 0){
            userTempId = tokenList.get(0);
        }else {
            //请求头没有,则从cookie中获取
            MultiValueMap<String, HttpCookie> cookieMultiValueMap = request.getCookies();
            HttpCookie userTempIdCookie = cookieMultiValueMap.getFirst("userTempId");
            if (null != userTempIdCookie){
                userTempId = URLDecoder.decode(userTempIdCookie.getValue());
            }
        }
        return userTempId;
    }

    /**
     * 获取用户id
     */
    private String getUserId(ServerHttpRequest request){
        String userId = "";
        String token = "";
        //从cookie中获取token
        MultiValueMap<String, HttpCookie> cookieMultiValueMap = request.getCookies();
        HttpCookie cookie = cookieMultiValueMap.getFirst("token");
        if (null != cookie){
            token = URLDecoder.decode(cookie.getValue());//通过路径解码器解码出token
        }else{
            //cookie中没值,获取请求头的值
            List<String> list = request.getHeaders().get("token");
            if (null != list && list.size() > 0){
                token = list.get(0);
            }
        }
        if (!StringUtils.isEmpty(token)){
            //通过feign获取userId
            userId = userFeignClient.getUserId(token);
        }
        return userId;
    }

    /***
     * 过滤器打印结果给页面
     */
    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum) {
        // 讲返回结果内容封装给response
        Result<Object> result = Result.build(null, resultCodeEnum);
        byte[] bits = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer wrap = response.bufferFactory().wrap(bits);
        // 设置response返回内容的编码格式
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        // 输出到页面
        return response.writeWith(Mono.just(wrap));
    }
}
