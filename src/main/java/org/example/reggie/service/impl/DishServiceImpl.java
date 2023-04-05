package org.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.R;
import org.example.reggie.dto.DishDto;
import org.example.reggie.entity.Dish;
import org.example.reggie.entity.DishFlavor;
import org.example.reggie.mapper.DishMapper;
import org.example.reggie.service.DishFlavorService;
import org.example.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishService dishService;

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

    /**
     * 根据id查询菜品信息和口味信息
     * @param id 菜品id
     * @return 菜品信息
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {

            // 查询菜品信息
            Dish dish = this.getById(id);

            // 转换为dto
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);

            // 查询菜品口味信息
            List<DishFlavor> dishFlavors = dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>()
                    .eq(DishFlavor::getDishId, id));

            // 设置菜品口味信息
            dishDto.setFlavors(dishFlavors);

            return dishDto;
    }

    /**
     * 更新菜品信息和口味信息
     * @param dishDto 菜品信息
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {

        // 更新菜品信息
        this.updateById(dishDto);

        // 删除菜品口味信息
        dishFlavorService.remove(new LambdaQueryWrapper<DishFlavor>()
                .eq(DishFlavor::getDishId, dishDto.getId()));

        // 添加菜品口味信息
        dishDto.getFlavors().forEach(dishFlavor -> {
            dishFlavor.setDishId(dishDto.getId());
        });
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    /**
     * 根据id删除菜品信息和口味信息
     * @param id 菜品id
     */
    @Transactional
    @Override
    public void removeByIdWithFlavor(Long id) {

            // 删除菜品信息
            this.removeById(id);

            // 删除菜品口味信息
            dishFlavorService.remove(new LambdaQueryWrapper<DishFlavor>()
                    .eq(DishFlavor::getDishId, id));
    }


    /**
     * 根据id更新菜品状态
     * @param status 菜品状态
     * @param ids 菜品id
     */
    @Override
    public void updateStatus(Integer status, List<Long> ids) {

            LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishLambdaQueryWrapper.in(ids!=null,Dish::getId, ids);

            List<Dish> dishList = dishService.list(dishLambdaQueryWrapper);

            dishList.forEach(dish -> {
                dish.setStatus(status);
                dishService.updateById(dish);
            });
    }


}
