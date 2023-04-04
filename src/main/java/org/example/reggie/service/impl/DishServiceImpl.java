package org.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.R;
import org.example.reggie.dto.DishDto;
import org.example.reggie.entity.Dish;
import org.example.reggie.mapper.DishMapper;
import org.example.reggie.service.DishFlavorService;
import org.example.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 添加菜品
     * @param dishDto 菜品信息
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {

        // 添加菜品
        this.save(dishDto);

        dishDto.getFlavors().forEach(dishFlavor -> {
            dishFlavor.setDishId(dishDto.getId());
        });

        // 添加菜品口味
        dishFlavorService.saveBatch(dishDto.getFlavors());

    }


}
