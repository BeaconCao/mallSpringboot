package com.how2java.tmall.interceptor;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.User;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class OtherInterceptor implements HandlerInterceptor {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OrderItemService orderItemService;


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        HttpSession session = httpServletRequest.getSession();
        User user = (User) session.getAttribute("user");

        //获取购物车中的数量
        int cartTotalItemNumber=0;
        if (null != user) {
            List<OrderItem> orderItems = orderItemService.listByUserAndOrderIsNull(user);
            for (OrderItem orderItem : orderItems) {
                cartTotalItemNumber+=orderItem.getNumber();
            }
        }
        //获取在搜索框下面展示的分类名集合
        List<Category> categories = categoryService.list();
        //获取servletContext地址
        String contextPath = httpServletRequest.getContextPath();
        //将以上三组数据返回给前端页面。
        httpServletRequest.getServletContext().setAttribute("categories_below_search",categories);
        httpServletRequest.getServletContext().setAttribute("contextPath",contextPath);
        session.setAttribute("cartTotalItemNumber",cartTotalItemNumber);
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
