package org.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.CustomException;
import org.example.reggie.dto.SetmealDto;
import org.example.reggie.entity.Setmeal;
import org.example.reggie.entity.SetmealDish;
import org.example.reggie.mapper.SetmealMapper;
import org.example.reggie.service.SetmealDishService;
import org.example.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐,并新增套餐菜品关联
     *
     * @param setmealDto 套餐信息
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {

        log.info("新增套餐: {}", setmealDto);

        // 保存套餐
        this.save(setmealDto);

        // 套餐菜品关联
        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
        setmealDishList.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealDto.getId());
        });

        // 批量保存
        setmealDishService.saveBatch(setmealDishList);

    }

    /**
     * 删除套餐,并删除套餐菜品关联
     *
     * @param ids 套餐id
     */
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {

        // 查询套餐状态，如果是上架状态，不允许删除
        List<Setmeal> setmealList = this.listByIds(ids);

        // 判断是否有上架状态的套餐
        boolean isOnShelf = setmealList.stream().anyMatch(setmeal -> setmeal.getStatus() == 1);

        // 如果有上架状态的套餐，不允许删除
        if (isOnShelf) {
            throw new CustomException("有上架状态的套餐，不允许删除");
        }

        // 删除套餐
        this.removeByIds(ids);

        // 删除套餐菜品关联
        setmealDishService.remove(new LambdaQueryWrapper<SetmealDish>()
                .in(SetmealDish::getSetmealId, ids));


    }
}
