package com.huajuan.mall.controller;

import com.github.pagehelper.PageInfo;
import com.huajuan.mall.consts.MallConst;
import com.huajuan.mall.form.OrderCreateForm;
import com.huajuan.mall.pojo.User;
import com.huajuan.mall.service.IOrderService;
import com.huajuan.mall.vo.OrderVo;
import com.huajuan.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @PostMapping("/orders")
    public ResponseVo<OrderVo> create(@Valid @RequestBody OrderCreateForm form,
                                      HttpSession session) {
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return orderService.create(user.getId(), form.getShippingId());
    }

    @GetMapping("/orders")
    public ResponseVo<PageInfo> list(@RequestParam Integer pageNum,
                                     @RequestParam Integer pageSize,
                                     HttpSession session) {
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return orderService.list(user.getId(), pageNum, pageSize);
    }

    @GetMapping("orders/{orderNo}")
    public ResponseVo<OrderVo> detail(@PathVariable Long orderNo,
                                      HttpSession session) {
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return orderService.detail(user.getId(), orderNo);
    }

    @PutMapping("/orders/{orderNO}")
    public ResponseVo cancel(@PathVariable Long orderNO,
                             HttpSession session) {
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return orderService.cancel(user.getId(), orderNO);
     }
}
