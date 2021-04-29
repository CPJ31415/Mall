package com.huajuan.mall.listener;

import com.google.gson.Gson;
import com.huajuan.mall.pojo.PayInfo;
import com.huajuan.mall.service.IOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * todo
 * pay项目提供一个client.jar, mall引用jar包
 */
@Component
@RabbitListener(queues = "payNotify")
public class PayMsgListener {

    @Autowired
    private IOrderService orderService;

    @RabbitHandler
    public void process(String msg) {

        PayInfo payInfo = new Gson().fromJson(msg, PayInfo.class);
        if (payInfo.getPlatformStatus().equals("SUCCESS")) {
            //修改订单里的状态
            orderService.paid(payInfo.getOrderNo());
        }
    }
}
