package com.huajuan.mall.service.impl;

import com.google.gson.Gson;
import com.huajuan.mall.dao.ProductMapper;
import com.huajuan.mall.enums.ProductStatusEnum;
import com.huajuan.mall.enums.ResponseEnum;
import com.huajuan.mall.form.CartAddForm;
import com.huajuan.mall.form.CartUpdateForm;
import com.huajuan.mall.pojo.Product;
import com.huajuan.mall.service.ICartService;
import com.huajuan.mall.vo.Cart;
import com.huajuan.mall.vo.CartProductVo;
import com.huajuan.mall.vo.CartVo;
import com.huajuan.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements ICartService {

    private final static String CART_REDIS_KEY_TEMPLATE = "cart_%d";

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final Gson gson = new Gson();

    @Override
    public ResponseVo<CartVo> add(Integer uid, CartAddForm form) {

        Integer quantity = 1;
        Product product = productMapper.selectByPrimaryKey(form.getProductId());

        //是否存在
        if (product == null) {
            return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST);
        }

        //是否正常在售
        if (!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())) {
            return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
        }

        //库存是否充足
        if (product.getStock() <= 0) {
            return ResponseVo.error(ResponseEnum.PRODUCT_STOCK_ERROR);
        }

        //写入redis
        //key: cart_1
        //存入id, 数量, 是否选中
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        Cart cart;
        String value = opsForHash.get(redisKey, String.valueOf(product.getId()));
        if (StringUtils.isEmpty(value)) {
            //没有该商品
            cart = new Cart(product.getId(), quantity, form.getSelected());
        } else {
            //数量+1
            cart = gson.fromJson(value, Cart.class);
            cart.setQuantity(cart.getQuantity() + quantity);
        }

        opsForHash.put(String.format(CART_REDIS_KEY_TEMPLATE, uid),
                String.valueOf(product.getId()),
                gson.toJson(cart));

        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> list(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        boolean selectAll = true;
        Integer cartTotalQuantity = 0;
        BigDecimal cartTotalPrice = BigDecimal.ZERO;

        Map<String, String> entries = opsForHash.entries(redisKey);

        CartVo cartVo = new CartVo();
        List<CartProductVo> cartProductVoList = new ArrayList<>();

        List<String> productIdList = new ArrayList<>();


        //select in  //不要在循环里面查询数据
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            productIdList.add(String.valueOf(entry.getKey()));
        }

        List<Product> productList = new ArrayList<>();
        if (!productIdList.isEmpty()) {
            productList = productMapper.selectByIdList(productIdList);
        }

        for (Product product : productList) {
            if (product != null) {
                Cart cart = gson.fromJson(entries.get(String.valueOf(product.getId())), Cart.class);
                CartProductVo cartProductVo = new CartProductVo(product.getId(),
                        cart.getQuantity(),
                        product.getName(),
                        product.getSubtitle(),
                        product.getMainImage(),
                        product.getPrice(),
                        product.getStatus(),
                        product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
                        product.getStock(),
                        cart.getProductSelected()
                );
                cartProductVoList.add(cartProductVo);

                //有一个没选就不是全选
                if (!cart.getProductSelected()) {
                    selectAll = false;
                }

                //选中的总价
                if (cart.getProductSelected()) {
                    cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice());

                }

                cartTotalQuantity += cart.getQuantity();
            }
        }

        cartVo.setSelectedAll(selectAll);
        cartVo.setCartTotalQuantity(cartTotalQuantity);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        return ResponseVo.success(cartVo);
    }

    @Override
    public ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm form) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        Cart cart;
        String value = opsForHash.get(redisKey, String.valueOf(productId));
        if (StringUtils.isEmpty(value)) {
            //没有该商品
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);
        }

        //有该商品, 修改内容
        cart = gson.fromJson(value, Cart.class);
        if (form.getQuantity() != null && form.getQuantity() >= 0) {
            cart.setQuantity(form.getQuantity());
        }
        if (form.getSelected() != null) {
            cart.setProductSelected(form.getSelected());
        }

        opsForHash.put(redisKey, String.valueOf(productId), gson.toJson(cart));

        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> delete(Integer uid, Integer productId) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        Cart cart;
        String value = opsForHash.get(redisKey, String.valueOf(productId));
        if (StringUtils.isEmpty(value)) {
            //没有该商品
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);
        }

        opsForHash.delete(redisKey, String.valueOf(productId));

        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> selectAll(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        for (Cart cart : listForCart(uid)) {
            cart.setProductSelected(true);
            opsForHash.put(redisKey,
                    String.valueOf(cart.getProductId()),
                    gson.toJson(cart));
        }

        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> unSelectAll(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        for (Cart cart : listForCart(uid)) {
            cart.setProductSelected(false);
            opsForHash.put(redisKey,
                    String.valueOf(cart.getProductId()),
                    gson.toJson(cart));
        }

        return list(uid);
    }

    @Override
    public ResponseVo<Integer> sum(Integer uid) {
        Integer sum = listForCart(uid).stream()
                .map(Cart::getQuantity)
                .reduce(0, Integer::sum);

        return ResponseVo.success(sum);
    }

    public List<Cart> listForCart(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(redisKey);

        List<Cart> cartList = new ArrayList<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            cartList.add(gson.fromJson(entry.getValue(), Cart.class));
        }

        return cartList;
    }

}
