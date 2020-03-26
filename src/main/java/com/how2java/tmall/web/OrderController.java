package com.how2java.tmall.web;

import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.service.OrderItemService;
import com.how2java.tmall.service.OrderService;
import com.how2java.tmall.util.Page4Navigator;
import com.how2java.tmall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;

    @GetMapping("/orders")
    public Page4Navigator<Order> list(
            @RequestParam(name = "start",defaultValue = "0") int start,
            @RequestParam(name = "size",defaultValue = "5") int size
    ) {
        start = start < 0 ? 0 : start;
        Page4Navigator<Order> bean = orderService.list(start, size, 5);
        orderItemService.fill(bean.getContent());
        orderService.removeOrderFromOrderItem(bean.getContent());
        return bean;

    }

    @PutMapping("/deliveryOrder/{oid}")
    public Object delivery(
            @PathVariable(name = "oid")int oid
    ) {
        Order order = orderService.get(oid);
        order.setDeliveryDate(new Date());
        order.setStatus(OrderService.waitConfirm);
        orderService.update(order);
        return Result.success();
    }
}
