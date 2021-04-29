package com.huajuan.mall.controller;

import com.huajuan.mall.consts.MallConst;
import com.huajuan.mall.form.ShippingForm;
import com.huajuan.mall.pojo.User;
import com.huajuan.mall.service.IShippingService;
import com.huajuan.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
public class ShippingService {

    @Autowired
    private IShippingService shippingService;

    @PostMapping("/shippings")
    public ResponseVo add(@Valid @RequestBody ShippingForm form,
                          HttpSession session) {
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return shippingService.add(user.getId(), form);
    }

    @DeleteMapping("/shippings/{shippingId}")
    public ResponseVo delete(@PathVariable Integer shippingId,
            HttpSession session) {
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return shippingService.delete(user.getId(), shippingId);
    }

    @PutMapping("/shippings/{shippingId}")
    public ResponseVo update(@PathVariable Integer shippingId,
                             @Valid @RequestBody ShippingForm form,
                             HttpSession session) {
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return shippingService.update(user.getId(), shippingId, form);
    }

    @GetMapping("shippings")
    public ResponseVo list(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                           @RequestParam(required = false, defaultValue = "10") Integer paheSize,
                           HttpSession session) {
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return shippingService.list((user.getId()), pageNum, paheSize);
    }
}
