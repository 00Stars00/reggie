package org.example.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.entity.SetmealDish;
import org.example.reggie.mapper.SetmealDishMapper;
import org.example.reggie.service.SetmealDishService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
