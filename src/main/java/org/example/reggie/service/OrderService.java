package org.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {

    /**
     * 提交订单
     *
     * @param orders 订单信息
     */
    public void submit(Orders orders);
}
