package org.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.example.reggie.common.R;
import org.example.reggie.dto.DishDto;
import org.example.reggie.entity.Category;
import org.example.reggie.entity.Dish;
import org.example.reggie.entity.DishFlavor;
import org.example.reggie.service.CategoryService;
import org.example.reggie.service.DishFlavorService;
import org.example.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 添加菜品
     *
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
     *
     * @param page     页码
     * @param pageSize 每页大小
     * @return 菜品信息列表
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {

        log.info("菜品信息分页查询: page = {}, pageSize = {}, name = {}", page, pageSize, name);

        // 分页构造器
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();


        // 条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 条件查询
        dishLambdaQueryWrapper.like(StringUtils.isNotBlank(name), Dish::getName, name);

        // 排序
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        // 分页查询
        dishService.page(dishPage, dishLambdaQueryWrapper);

        // 转换
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        List<Dish> records = dishPage.getRecords();
        List<DishDto> dishDtoList = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = (Long) item.getCategoryId();

            Category category = categoryService.getById(categoryId);

            if (category != null) {

                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);

            }

            return dishDto;

        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoList);

        // 返回
        log.info("菜品信息分页查询成功");
        return R.success(dishDtoPage);

    }


    /**
     * 根据id查询菜品信息和口味
     *
     * @param id 菜品id
     * @return 成功信息
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {

        log.info("查询菜品信息: id = {}", id);

        // 查询菜品信息
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        // 返回
        log.info("查询菜品信息成功");
        return R.success(dishDto);

    }


    /**
     * 修改菜品信息
     *
     * @param dishDto 菜品信息
     * @return 成功信息
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {

        log.info("修改菜品信息: {}", dishDto);

        // 修改菜品信息
        dishService.updateWithFlavor(dishDto);

        // 清除redis缓存
        String key = "dish_" + dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        // 返回
        log.info("修改菜品信息成功");
        return R.success("修改菜品信息成功");

    }


    /**
     * 根据id删除菜品信息和口味信息
     *
     * @param ids 菜品id
     * @return 成功信息
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {

        log.info("删除菜品信息: id = {}", ids);

        // 删除菜品信息
        dishService.removeByIdWithFlavor(ids);

        // 返回
        log.info("删除菜品信息成功");
        return R.success("删除菜品信息成功");


    }


    /**
     * 修改菜品状态
     *
     * @param status 状态
     * @param ids    菜品id
     * @return 成功信息
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable("status") Integer status, @RequestParam List<Long> ids) {

        log.info("修改菜品状态: status = {}, ids = {}", status, ids);

        // 修改菜品状态
        dishService.updateStatus(status, ids);

        // 返回
        log.info("修改菜品状态成功");
        return R.success("修改菜品状态成功");

    }


    /**
     * 根据条件查询菜品列表
     *
     * @param dish 菜品信息
     * @return 菜品列表
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {

        log.info("查询菜品列表: {}", dish);

        // 定义菜品列表
        List<DishDto> dishDtoList = null;

        // 从redis中获取菜品列表
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get("dish_" + dish.getCategoryId() + "_" + dish.getStatus());

        // 如果redis中有菜品列表, 则直接返回
        if (dishDtoList != null) {
            log.info("从redis中获取菜品列表成功");
            return R.success(dishDtoList);
        }

        // 如果redis中没有菜品列表, 则从数据库中查询
        // 查询菜品列表
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 条件查询
        dishLambdaQueryWrapper.eq(Dish::getStatus, 1);
        dishLambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());

        // 排序
        dishLambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        // 查询
        List<Dish> dishList = dishService.list(dishLambdaQueryWrapper);

        // 转换
        dishDtoList = dishList.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = (Long) item.getCategoryId();

            Category category = categoryService.getById(categoryId);

            if (category != null) {

                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);

            }

            // 当前菜品的ID
            Long dishId = item.getId();

            // 查询口味列表
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);

            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

            dishDto.setFlavors(dishFlavorList);

            return dishDto;

        }).collect(Collectors.toList());

        // 将菜品列表存入redis
        redisTemplate.opsForValue().set("dish_" + dish.getCategoryId() + "_" + dish.getStatus(), dishDtoList, 60, TimeUnit.MINUTES);

        // 返回
        log.info("查询菜品列表成功: {}", dishDtoList);
        return R.success(dishDtoList);

    }
}
