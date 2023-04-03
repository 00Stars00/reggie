package org.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.R;
import org.example.reggie.entity.Category;
import org.example.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 添加分类
     * @param category 分类信息
     * @return 成功信息
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {

        log.info("添加分类");

        // 添加分类
        categoryService.save(category);

        // 返回
        log.info("添加成功");
        return R.success("添加成功");
    }


    /**
     * 分类信息分页查询
     * @param page 页码
     * @param size 每页大小
     * @return 分类信息列表
     */
    @PostMapping("/page")
    public R<Page<Category>> page(int page, int size) {

        log.info("分类信息分页查询");

        // 分页构造器
        Page<Category> categoryPage = new Page<>(page, size);

        // 条件构造器
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 排序
        categoryLambdaQueryWrapper.orderByAsc(Category::getSort);

        // 分页查询
        categoryService.page(categoryPage, categoryLambdaQueryWrapper);

        // 返回
        log.info("分类信息分页查询成功");
        return R.success(categoryPage);

    }

}
