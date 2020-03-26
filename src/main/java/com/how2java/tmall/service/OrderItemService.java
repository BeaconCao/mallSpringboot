package com.how2java.tmall.service;


import com.how2java.tmall.dao.OrderItemDao;
import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.User;
import com.how2java.tmall.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "orderItems")
public class OrderItemService {
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private ProductImageService productImageService;


    @CacheEvict(allEntries = true)
    public void add(OrderItem orderItem) {
        orderItemDao.save(orderItem);
    }
    @CacheEvict(allEntries = true)
    public void update(OrderItem orderItem) {
        orderItemDao.save(orderItem);
    }
    @Cacheable(key = "'orderItems-oid'+#p0.id")
    public List<OrderItem> listByOrder(Order order) {
        return orderItemDao.findByOrderOrderByIdDesc(order);
    }

    public void fill(Order order) {
        OrderItemService orderItemService = SpringContextUtil.getBean(OrderItemService.class);
        List<OrderItem> orderItems = orderItemService.listByOrder(order);
        float total = 0 ;
        int totalNumber = 0 ;
        for (OrderItem orderItem : orderItems) {
            total += orderItem.getProduct().getPromotePrice();
            totalNumber += orderItem.getNumber();
            productImageService.setFirstProductImage(orderItem.getProduct());
        }
        order.setTotal(total);
        order.setTotalNumber(totalNumber);
        order.setOrderItems(orderItems);
    }

    public void fill(List<Order> orders) {
        for (Order order : orders) {
            fill(order);
        }
    }

    public int getSalesCount(Product product) {
        OrderItemService orderItemService = SpringContextUtil.getBean(OrderItemService.class);
        List<OrderItem> orderItems = orderItemService.listByProduct(product);
        int result=0;
        for (OrderItem orderItem : orderItems) {
                if (null != orderItem.getOrder() && null != orderItem.getOrder().getPayDate()) {
                    result += orderItem.getNumber();
                }
        }
        return  result;
    }
    @Cacheable(key = "'orderItems-pid-'+#p0.id")
    public List<OrderItem> listByProduct(Product product) {
        return orderItemDao.findByProduct(product);
    }

    @Cacheable(key="'orderItems-uid-'+#p0.id")
    public List<OrderItem> listByUserAndOrderIsNull(User user) {
        return orderItemDao.findByUserAndOrderIsNull(user);
    }


    @Cacheable(key = "'orderItems-one-'+#p0")
    public OrderItem get(int id) {
        return  orderItemDao.getOne(id);
    }

    @CacheEvict(allEntries = true)
    public void delete(int oiid) {
        orderItemDao.delete(oiid);
    }
}
