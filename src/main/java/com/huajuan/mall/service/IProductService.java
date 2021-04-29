package com.huajuan.mall.service;

import com.github.pagehelper.PageInfo;
import com.huajuan.mall.vo.ProductDetailVo;
import com.huajuan.mall.vo.ResponseVo;

public interface IProductService {

    ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize);

    ResponseVo<ProductDetailVo> detail(Integer productId);
}
