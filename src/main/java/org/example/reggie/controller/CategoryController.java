package org.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.R;
import org.example.reggie.entity.Category;
import org.example.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * @param pageSize 每页大小
     * @return 分类信息列表
     */
    @GetMapping("/page")
    public R<Page<Category>> page(int page, int pageSize) {

        log.info("分类信息分页查询");

        // 分页构造器
        Page<Category> categoryPage = new Page<>(page, pageSize);

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


    /**
     * 根据id删除分类
     * @param ids 分类id
     * @return 成功信息
     */
    @DeleteMapping
    public R<String> delete(Long ids) {

        log.info("删除分类");

        // 删除
        categoryService.remove(ids);

        // 返回
        log.info("删除分类成功");
        return R.success("删除成功");
    }


    /**
     * 修改分类
     * @param category 分类信息
     * @return 成功信息
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {

        log.info("修改分类");

        // 修改
        categoryService.updateById(category);

        // 返回
        log.info("修改分类成功");
        return R.success("修改成功");
    }

    /**
     * 根据条件查询分类列表
     * @param category 查询条件
     * @return 分类列表
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {

            log.info("查询分类列表");

            // 条件构造器
            LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();

            // 查询条件
            categoryLambdaQueryWrapper.eq(category.getType() != null, Category::getType, category.getType());

            // 排序
            categoryLambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

            // 查询
            List<Category> categoryList = categoryService.list(categoryLambdaQueryWrapper);

            // 返回
            log.info("查询分类列表成功");
            return R.success(categoryList);

    }

}
