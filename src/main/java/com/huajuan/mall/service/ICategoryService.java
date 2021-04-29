package com.huajuan.mall.service;

import com.huajuan.mall.vo.CategoryVo;
import com.huajuan.mall.vo.ResponseVo;

import java.util.List;
import java.util.Set;

public interface ICategoryService {

    ResponseVo<List<CategoryVo>> selectAll();

    void findSubCategoryId(Integer id, Set<Integer> resultSet);

}
