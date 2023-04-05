package org.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.reggie.dto.DishDto;
import org.example.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    /**
     * 添加菜品
     * @param dishDto 菜品信息
     */
    public void saveWithFlavor(DishDto dishDto);

    /**
     * 根据id查询菜品信息和口味信息
     * @param id 菜品id
     * @return 菜品信息
     */
    public DishDto getByIdWithFlavor(Long id);

    /**
     * 更新菜品信息和口味信息
     * @param dishDto 菜品信息
     */
    public void updateWithFlavor(DishDto dishDto);

    /**
     * 根据id删除菜品口味信息
     * @param id 菜品口味id
     */
    void removeByIdWithFlavor(List<Long> id);

    /**
     * 根据id更新菜品状态
     * @param status 菜品状态
     * @param id 菜品id
     */
    void updateStatus(Integer status, List<Long> id);
}
