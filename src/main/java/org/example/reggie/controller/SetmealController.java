package org.example.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.R;
import org.example.reggie.dto.SetmealDto;
import org.example.reggie.entity.Category;
import org.example.reggie.entity.Setmeal;
import org.example.reggie.service.CategoryService;
import org.example.reggie.service.SetmealDishService;
import org.example.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增套餐
     *
     * @param setmealDto 套餐信息
     * @return 新增结果
     */
    @PostMapping
    @CacheEvict(value = "setmealList", allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto) {

        log.info("新增套餐: {}", setmealDto);

        // 保存套餐
        setmealService.saveWithDish(setmealDto);

        // 返回
        return R.success("新增成功");
    }


    /**
     * 套餐分页查询
     *
     * @param page 分页参数
     * @return 分页结果
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {

        log.info("分页查询套餐: page = {}, pageSize = {}, name = {}", page, pageSize, name);

        // 分页构造器
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        // 查询条件构造器
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 根据套餐名称模糊查询
        setmealLambdaQueryWrapper.like(name != null, Setmeal::getName, name);

        // 排序
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        // 分页查询
        setmealService.page(setmealPage, setmealLambdaQueryWrapper);

        // 对象拷贝
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        List<Setmeal> setmealList = setmealPage.getRecords();

        // 遍历套餐列表
        List<SetmealDto> setmealDtoList = setmealList.stream().map(setmeal -> {

            // 创建套餐DTO对象
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);

            // 获取套餐分类ID
            Long CategoryId = setmeal.getCategoryId();

            // 根据分类ID查询分类
            Category category = categoryService.getById(CategoryId);

            // 设置分类名称
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }

            // 返回
            return setmealDto;

        }).collect(Collectors.toList());

        // 设置套餐DTO列表
        setmealDtoPage.setRecords(setmealDtoList);

        // 返回
        log.info("分页查询成功: {}", setmealDtoPage.getRecords());
        return R.success(setmealDtoPage);

    }


    /**
     * 删除套餐
     *
     * @param ids 套餐ID列表
     * @return 删除结果
     */
    @DeleteMapping
    @CacheEvict(value = "setmealList", allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids) {

        log.info("删除套餐: {}", ids);

        // 删除套餐
        setmealService.removeWithDish(ids);

        // 返回
        return R.success("删除成功");
    }


    /**
     * 根据条件查询套餐列表
     *
     * @param setmeal 查询条件
     * @return 套餐列表
     */
    @Cacheable(value = "setmealList", key = "#setmeal.categoryId + '_' + #setmeal.status")
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {

        log.info("查询套餐列表: {}", setmeal);

        // 查询条件构造器
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 根据套餐名称模糊查询
        setmealLambdaQueryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        setmealLambdaQueryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus, setmeal.getStatus());

        // 排序
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        // 查询
        List<Setmeal> setmealList = setmealService.list(setmealLambdaQueryWrapper);

        // 返回
        log.info("查询成功: {}", setmealList);
        return R.success(setmealList);

    }

}


