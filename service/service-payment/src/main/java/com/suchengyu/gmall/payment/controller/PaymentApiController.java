package com.suchengyu.gmall.payment.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.suchengyu.gmall.model.enums.PaymentStatus;
import com.suchengyu.gmall.model.enums.PaymentType;
import com.suchengyu.gmall.model.payment.PaymentInfo;
import com.suchengyu.gmall.payment.config.AlipayConfig;
import com.suchengyu.gmall.payment.service.AlipayService;
import com.suchengyu.gmall.payment.service.PaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * PaymentApiController
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-08
 * @Description:
 */
@Api(description = "支付宝支付接口")
@Controller
@RequestMapping("/api/payment")
public class PaymentApiController {
    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private AlipayService alipayService;
    @Autowired
    private PaymentService paymentService;

    @ApiOperation(value = "支付宝异步回调  必须使用内网穿透")
    @RequestMapping("/callback/notify")
    @ResponseBody
    public String alipayNotify(@RequestParam Map<String,String> paramMap){
        System.out.println("支付宝回调来了");
        boolean signVerified = false;//调用SDK验证签名

        try {
            signVerified  = AlipaySignature.rsaCheckV1(paramMap, AlipayConfig.alipay_public_key, AlipayConfig.charset,AlipayConfig.sign_type);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //交易状态
        String trade_status = paramMap.get("trade_status");
        String out_trade_no = paramMap.get("out_trade_no");
        if (signVerified){
            // TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，
            //  校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
            if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)){
                // 但是，如果交易记录表中 PAID 或者 CLOSE  获取交易记录中的支付状态 通过outTradeNo来查询数据
                // select * from paymentInfo where out_trade_no=?
                PaymentInfo paymentInfo = paymentService.getPaymentInfo(out_trade_no, PaymentType.ALIPAY.name());
                if (paymentInfo.getPaymentStatus() == PaymentStatus.PAID.name()
                        || paymentInfo.getPaymentStatus() == PaymentStatus.ClOSED.name()){
                    return "failure";
                }
                // 正常的支付成功，我们应该更新交易记录状态
                paymentService.paySuccess(out_trade_no,PaymentType.ALIPAY.name(),paramMap);
                return "success";
            }
        }else {
            // TODO 验签失败则记录异常日志，并在response中返回failure.
            return "failure";
        }
        return "failure";
    }


    @ApiOperation(value = "支付宝同步回调")
    @RequestMapping("/alipay/callback/return")
    public String callBack(HttpServletRequest request){
        String queryString = request.getQueryString();
        String trade_no = request.getParameter("trade_no");
        String out_trade_no = request.getParameter("out_trade_no");
        String sign = request.getParameter("sign");

        PaymentInfo paymentInfo = new PaymentInfo();

        paymentInfo.setCallbackContent(queryString);
        paymentInfo.setTradeNo(trade_no);
        paymentInfo.setOutTradeNo(out_trade_no);
        // 正常的支付成功，我们应该更新交易记录状态
        paymentService.updatePayment(paymentInfo);
        //同步回调给用户展示信息
        return "redirect:" + AlipayConfig.return_order_url;
    }

    @ApiOperation(value = "保存交易记录,生成支付二维码")
    @RequestMapping("/alipay/submit/{orderId}")
    @ResponseBody
    public String alipaySubmit(@PathVariable("orderId") Long orderId){
        String from = "";
        try {
            //生成表单,发送延迟队列消息进行幂等性校验
            from = alipayService.alipaySubmit(orderId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return from;
    }
}
