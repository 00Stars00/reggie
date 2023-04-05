package org.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.reggie.dto.SetmealDto;
import org.example.reggie.entity.Setmeal;

import java.util.List;


public interface SetmealService extends IService<Setmeal>{


    /**
     * 新增套餐,并新增套餐菜品关联
     * @param setmealDto 套餐信息
     */
    public void saveWithDish(SetmealDto setmealDto);


    /**
     * 删除套餐,并删除套餐菜品关联
     * @param ids 套餐id
     */
    public void removeWithDish(List<Long> ids);

}
