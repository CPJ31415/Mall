package com.huajuan.mall.service.impl;

import com.github.pagehelper.PageInfo;
import com.huajuan.mall.MallApplicationTests;
import com.huajuan.mall.enums.ResponseEnum;
import com.huajuan.mall.service.IProductService;
import com.huajuan.mall.vo.ProductDetailVo;
import com.huajuan.mall.vo.ResponseVo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductServiceImplTest extends MallApplicationTests {

    @Autowired
    private IProductService productService;

    @Test
    public void list() {
        ResponseVo<PageInfo> responseVo = productService.list(null, 2, 2);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    public void detail() {
        ResponseVo<ProductDetailVo> responseVo = productService.detail(26);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

}