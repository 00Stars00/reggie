package org.example.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.BaseContext;
import org.example.reggie.common.R;
import org.example.reggie.entity.Orders;
import org.example.reggie.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    /**
     * 提交订单
     *
     * @param orders 订单信息
     * @return 提交订单结果
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {

        log.info("提交订单:{}", orders);

        orderService.submit(orders);


        return R.success("提交订单成功");
    }


    /**
     * 订单分页查询
     * @param page 页码
     * @param pageSize 每页数量
     * @return 订单分页信息
     */
    @GetMapping("/userPage")
    public R<Page<Orders>> page(int page,int pageSize){

        log.info("订单分页查询:page={},pageSize={}",page,pageSize);

        Page<Orders> ordersPage = new Page<>(page,pageSize);

        String userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();

        ordersLambdaQueryWrapper.eq(Orders::getUserId,userId);

        ordersLambdaQueryWrapper.orderByDesc(Orders::getOrderTime);

        orderService.page(ordersPage,ordersLambdaQueryWrapper);

        return R.success(ordersPage);


    }

}
