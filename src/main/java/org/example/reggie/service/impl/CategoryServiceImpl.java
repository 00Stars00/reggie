package org.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.CustomException;
import org.example.reggie.entity.Category;
import org.example.reggie.entity.Dish;
import org.example.reggie.entity.Setmeal;
import org.example.reggie.mapper.CategoryMapper;
import org.example.reggie.service.CategoryService;
import org.example.reggie.service.DishService;
import org.example.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    // 注入菜品service
    @Autowired
    private DishService dishService;

    // 注入套餐service
    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类
     * 删除分类时，需要判断该分类下是否有套餐或菜品，如果有套餐或菜品，则不允许删除
     *
     * @param id 分类id
     */
    @Override
    public void remove(Long id) {

        log.info("删除分类，id为：{}", id);

        // 判断该分类下是否有套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        log.info("该分类下有{}个套餐", setmealCount);
        if (setmealCount > 0) {
            throw new CustomException("该分类下有套餐，不允许删除");
        }

        // 判断该分类下是否有菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        log.info("该分类下有{}个菜品", dishCount);
        if (dishCount > 0) {
            throw new CustomException("该分类下有菜品，不允许删除");
        }


        // 如果该分类下没有套餐和菜品，则允许删除
        super.removeById(id);

    }
}
