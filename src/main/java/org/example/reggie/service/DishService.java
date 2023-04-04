package org.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.reggie.dto.DishDto;
import org.example.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    /**
     * 添加菜品
     * @param dishDto 菜品信息
     */
    public void saveWithFlavor(DishDto dishDto);
}
