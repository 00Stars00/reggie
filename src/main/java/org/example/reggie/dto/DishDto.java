package org.example.reggie.dto;

import lombok.Data;
import org.example.reggie.entity.Dish;
import org.example.reggie.entity.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
