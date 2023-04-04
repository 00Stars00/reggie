package org.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.example.reggie.common.R;
import org.example.reggie.dto.DishDto;
import org.example.reggie.entity.Dish;
import org.example.reggie.service.DishFlavorService;
import org.example.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 添加菜品
     * @param dishDto 菜品信息
     * @return 成功信息
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {

        log.info("添加菜品: {}", dishDto);

        // 添加菜品
        dishService.saveWithFlavor(dishDto);

        // 返回
        log.info("添加菜品成功");
        return R.success("添加菜品成功");
    }


    /**
     * 菜品信息分页查询
     * @param page 页码
     * @param pageSize 每页大小
     * @return 菜品信息列表
     */
    @GetMapping("/page")
    public R<Page<Dish>> page(int page, int pageSize, String name){

        log.info("菜品信息分页查询: page = {}, pageSize = {}, name = {}", page, pageSize, name);

        // 分页构造器
        Page<Dish> dishPage = new Page<>(page, pageSize);

        // 条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 条件查询
        dishLambdaQueryWrapper.like(StringUtils.isNotBlank(name), Dish::getName, name);

        // 排序
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        // 分页查询
        dishService.page(dishPage, dishLambdaQueryWrapper);

        // 返回
        log.info("菜品信息分页查询成功");
        return R.success(dishPage);

    }



}
