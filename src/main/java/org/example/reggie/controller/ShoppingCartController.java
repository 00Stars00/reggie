package org.example.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.BaseContext;
import org.example.reggie.common.CustomException;
import org.example.reggie.common.R;
import org.example.reggie.entity.ShoppingCart;
import org.example.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * 添加购物车
     *
     * @param shoppingCart 购物车信息
     * @return 添加结果
     */
    @PostMapping("/add")
    private R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("添加购物车: {}", shoppingCart);

        Long currentId = Long.valueOf(BaseContext.getCurrentId());
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);

        // 如果是菜品
        if (dishId != null) {

            // 判断购物车中是否已经存在该菜品
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);
        }

        // 如果是套餐
        if (setmealId != null) {

            // 判断购物车中是否已经存在该套餐
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
        }

        ShoppingCart cartServiceOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);

        // 如果购物车中已经存在该菜品或套餐
        if (cartServiceOne != null) {
            // 则将数量加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            // 否则直接添加
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);
    }


    /**
     * 查询购物车列表
     *
     * @return 购物车列表
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info("查询购物车列表");

        // 获取当前用户id
        Long currentId = Long.valueOf(BaseContext.getCurrentId());

        // 查询购物车列表
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);

        // 按照创建时间降序排列
        shoppingCartLambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);

        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);

        return R.success(shoppingCartList);
    }

    /**
     * 删除购物车
     *
     * @param shoppingCart 购物车信息
     * @return 删除结果
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        log.info("删除购物车: {}", shoppingCart);

        // 获取当前用户id
        Long currentId = Long.valueOf(BaseContext.getCurrentId());
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);

        // 如果是菜品
        if (dishId != null) {

            // 判断购物车中是否已经存在该菜品
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);
        }

        // 如果是套餐
        if (setmealId != null) {

            // 判断购物车中是否已经存在该套餐
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
        }

        ShoppingCart cartServiceOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);

        if (cartServiceOne.getNumber() > 1) {
            // 则将数量减一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number - 1);
            shoppingCartService.updateById(cartServiceOne);
        } else if (cartServiceOne.getNumber() == 1){
            // 否则直接删除
            cartServiceOne.setNumber(0);
            shoppingCartService.updateById(cartServiceOne);
            shoppingCartService.removeById(cartServiceOne.getId());
        }

        return R.success(cartServiceOne);
    }


    /**
     * 清空购物车
     *
     * @return 删除结果
     */
    @DeleteMapping("/clean")
    public R<String> clean() {

        log.info("清空购物车");

        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);

        return R.success("清空购物车成功");

    }

}
