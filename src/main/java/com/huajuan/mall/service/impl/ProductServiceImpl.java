package com.huajuan.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.huajuan.mall.dao.ProductMapper;
import com.huajuan.mall.pojo.Product;
import com.huajuan.mall.service.ICategoryService;
import com.huajuan.mall.service.IProductService;
import com.huajuan.mall.vo.ProductDetailVo;
import com.huajuan.mall.vo.ProductVo;
import com.huajuan.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.huajuan.mall.enums.ProductStatusEnum.DELETE;
import static com.huajuan.mall.enums.ProductStatusEnum.OFF_SALE;
import static com.huajuan.mall.enums.ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE;

@Slf4j
@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize) {
        Set<Integer> categoryIdSet = new HashSet<>();
        if (categoryId != null) {
            categoryService.findSubCategoryId(categoryId, categoryIdSet);
            categoryIdSet.add(categoryId);
        }

        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectByCategoryIdSet(categoryIdSet);
        List<ProductVo> productVoList = new ArrayList<>();
        for (Product product : productList) {
            ProductVo productVo = new ProductVo();
            BeanUtils.copyProperties(product, productVo);
            productVoList.add(productVo);
        }

        //有修改!
        PageInfo pageInfo = new PageInfo<>(productVoList);

        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<ProductDetailVo> detail(Integer productId) {
        Product product = productMapper.selectByPrimaryKey(productId);


        if (product.getStatus().equals(OFF_SALE.getCode()) || product.getStatus().equals(DELETE.getCode())) {
            return ResponseVo.error(PRODUCT_OFF_SALE_OR_DELETE);
        }
        ProductDetailVo productDetailVo = new ProductDetailVo();
        BeanUtils.copyProperties(product, productDetailVo);

        //敏感数据处理
        productDetailVo.setStock(product.getStock() > 100 ? 100 : product.getStock());
        return ResponseVo.success(productDetailVo);
    }
}
