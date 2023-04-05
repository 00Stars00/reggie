package org.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.reggie.dto.SetmealDto;
import org.example.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal>{


    /**
     * 新增套餐,并新增套餐菜品关联
     * @param setmealDto 套餐信息
     */
    public void saveWithDish(SetmealDto setmealDto);

}
