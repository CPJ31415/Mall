package com.huajuan.mall.service;

import com.huajuan.mall.form.CartAddForm;
import com.huajuan.mall.form.CartUpdateForm;
import com.huajuan.mall.vo.Cart;
import com.huajuan.mall.vo.CartVo;
import com.huajuan.mall.vo.ResponseVo;

import java.util.List;

public interface ICartService {

    ResponseVo<CartVo> add(Integer uid, CartAddForm form);

    ResponseVo<CartVo> list(Integer uid);

    ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm form);

    ResponseVo<CartVo> delete(Integer uid, Integer productId);

    ResponseVo<CartVo> selectAll(Integer uid);

    ResponseVo<CartVo> unSelectAll(Integer uid);

    ResponseVo<Integer> sum(Integer uid);

    List<Cart> listForCart(Integer uid);
}

