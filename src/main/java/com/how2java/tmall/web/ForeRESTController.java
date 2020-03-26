package com.how2java.tmall.web;

import com.how2java.tmall.comparator.ProductComparator;
import com.how2java.tmall.pojo.*;
import com.how2java.tmall.service.*;
import com.how2java.tmall.util.Result;
import com.sun.media.sound.ModelAbstractOscillator;
import javafx.beans.binding.ObjectExpression;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ForeRESTController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductImageService productImageService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private PropertyValueService propertyValueService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private OrderService orderService;

    @GetMapping("/forehome")
    public Object home() {
        List<Category> categories = categoryService.list();
        productService.fill(categories);
        productService.fillByRow(categories);
        categoryService.removeCategoryFromProduct(categories);
        return categories;
    }

    @PostMapping("foreregister")
    public Object register(
            @RequestBody User user
    ) {

        String name = user.getName();
        String password = user.getPassword();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist = userService.isExist(name);
        if (exist) {
            return Result.fail("用户名已存在，请更换新ID");
        }
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2 ;
        String algorithmName = "md5";
        String encodedPassword = new SimpleHash(algorithmName, password, salt, times).toString();

        user.setPassword(encodedPassword);
        user.setSalt(salt);
        userService.add(user);
        return Result.success();

    }

    @PostMapping("/forelogin")
    public Object login(
            @RequestBody User userParam,
            HttpSession session
    ) {
        String name = userParam.getName();
        String password = userParam.getPassword();
        name = HtmlUtils.htmlEscape(name);

        UsernamePasswordToken token = new UsernamePasswordToken(name, password);
        Subject subject = SecurityUtils.getSubject();
        try  {
            subject.login(token);
            System.out.println(userParam);
            User user = userService.getByName(name);
            System.out.println(user);
            session.setAttribute("user", user);
            return Result.success();

        } catch (AuthenticationException e) {
            String message = "账号或密码错误";
            return Result.fail(message);
        }

    }

    @GetMapping("foreproduct/{pid}")
    public Object product(
            @PathVariable(name = "pid") int pid
    ) {

        Product product = productService.get(pid);

        //查询图片，然后设置给product
        List<ProductImage> productSingleImages = productImageService.listSingleProductImages(product);
        List<ProductImage> productDetailImages = productImageService.listDetailProductImages(product);
        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);
        //查询销量和评论数设置给product
        productService.setSaleAndReviewNumber(product);

        //设置图片
        productImageService.setFirstProductImage(product);
        //list出属性值和评论
        List<PropertyValue> propertyValues = propertyValueService.list(product);
        List<Review> reviews = reviewService.list(product);

        HashMap<String, Object> map = new HashMap<>();

        map.put("product", product);
        map.put("pvs", propertyValues);
        map.put("reviews", reviews);

        return Result.success(map);

    }

    @GetMapping("/forecheckLogin")
    public Result checkLogin(HttpSession session) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return Result.success();
        } else {
            return Result.fail("请登录！");
        }

    }

    @GetMapping("/forecategory/{cid}")
    public Object category(
            @PathVariable(name = "cid") int cid,
            String sort
    ) {
        Category category = categoryService.get(cid);
        productService.fill(category);
        productService.setSaleAndReviewNumber(category.getProducts());
        categoryService.removeCategoryFromProduct(category);
        Collections.sort(category.getProducts(), new ProductComparator(sort));
        return category;
    }


    @PostMapping("/foresearch")
    public List<Product> search(String keyword) {
        if (null == keyword) {
            keyword = "";
        }

        List<Product> products = productService.search(0, 20, keyword);
        productService.setSaleAndReviewNumber(products);
        productImageService.setFirstProductImage(products);
        return products;
    }

    @GetMapping("/forebuyone")
    public Object buyone(int pid, int num, HttpSession session) {
        return buyoneAndAddCart(pid, num, session);
    }


    private int buyoneAndAddCart(int pid, int num, HttpSession session) {
        Product product = productService.get(pid);
        int oiid = 0;
        boolean found = false;
        User user = (User) session.getAttribute("user");
        List<OrderItem> orderItems = orderItemService.listByUserAndOrderIsNull(user);
        if (orderItems != null) {
            for (OrderItem orderItem : orderItems) {
                if (orderItem.getProduct().getId() == product.getId()) {
                    orderItem.setNumber(orderItem.getNumber() + num);
                    orderItemService.update(orderItem);
                    found = true;
                    oiid = orderItem.getId();
                    break;
                }
            }
        }

        if (!found) {
            OrderItem orderItem = new OrderItem();
            orderItem.setNumber(num);
            orderItem.setProduct(productService.get(pid));
            orderItem.setUser(user);
            orderItemService.add(orderItem);
            oiid = orderItem.getId();
        }
        return oiid;

    }

    @GetMapping("/forebuy")
    public Object buy(String[] oiid, HttpSession session) {
        List<OrderItem> orderItems = new ArrayList<>();
        float total = 0;
        for (String strid : oiid) {
            int id = Integer.parseInt(strid);
            OrderItem orderItem = orderItemService.get(id);
            total += orderItem.getProduct().getPromotePrice() * orderItem.getNumber();
            orderItems.add(orderItem);
        }

        productImageService.setFirstProductImagesOnOrderItems(orderItems);

        session.setAttribute("ois", orderItems);

        Map<String, Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total);
        return Result.success(map);

    }

    @GetMapping("/foreaddCart")
    public Object addCart(int pid, int num, HttpSession session) {
        buyoneAndAddCart(pid, num, session);
        return Result.success();
    }

    @GetMapping("/forecart")
    public Object cart(HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<OrderItem> orderItems = orderItemService.listByUserAndOrderIsNull(user);
        productImageService.setFirstProductImagesOnOrderItems(orderItems);
        return orderItems;
    }

    @GetMapping("/foredeleteOrderItem")
    public Object deleteOrderItem(
            @RequestParam(name = "oiid") int oiid,
            HttpSession session
    ) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            return Result.fail("登录后再操作，谢谢~");
        }
        orderItemService.delete(oiid);
        return Result.success();
    }

    @GetMapping("forechangeOrderItem")
    public Object changeOrderItem(
            @RequestParam(name = "pid") int pid,
            @RequestParam(name = "num") int num,
            HttpSession session
    ) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            return Result.fail("未登录，请登录，亲~");
        }
        List<OrderItem> orderItems = orderItemService.listByUserAndOrderIsNull(user);
        for (OrderItem orderItem : orderItems) {
            if (pid == orderItem.getProduct().getId()) {
                orderItem.setNumber(num);
                orderItemService.update(orderItem);
                break;

            }
        }
        return Result.success();
    }

    @PostMapping("/forecreateOrder")
    public Object createOrder(@RequestBody Order order, HttpSession session) {
        //先判断一下是否登录
        User user = (User) session.getAttribute("user");
        if (null == user) {
            return Result.fail("未登录，请登录，亲~");
        }
        //获取并设置orderCode，设置订单创建时间，设置订单用户，设置订单状态。
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt() * 10000;
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(user);
        order.setStatus(OrderService.waitPay);


        List<OrderItem> ois = (List<OrderItem>) session.getAttribute("ois");
        float total = orderService.add(order, ois);
        //把oid、和total放进map回传给前端页面。
        Map<String, Object> map = new HashMap();
        map.put("oid", order.getId());
        map.put("total", total);

        return Result.success(map);

    }

    @GetMapping("/forebought")
    public Object bought(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            return Result.fail("请登录！");
        }
        List<Order> orders = orderService.listByUserWithoutDelete(user);
        orderService.removeOrderFromOrderItem(orders);
        return orders;
    }

    @GetMapping("/forepayed")
    public Object pay(
            @RequestParam(name = "oid") int oid
    ) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitConfirm);
        orderService.update(order);
        return Result.success();
    }

    @GetMapping("/foreconfirmPay")
    public Object confirmPay(int oid) {
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        orderService.cacl(order);
        orderService.removeOrderFromOrderItem(order);
        return order;
    }
    @GetMapping("foreorderConfirmed")
    public Object orderConfirmed( int oid) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.waitReview);
        o.setConfirmDate(new Date());
        orderService.update(o);
        return Result.success();
    }

    @GetMapping("forereview")
    public Object review(int oid) {
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        orderService.removeOrderFromOrderItem(order);
        Product product = order.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewService.list(product);
        productService.setSaleAndReviewNumber(product);
        Map<String,Object> map = new HashMap<>();
        map.put("p",product);
        map.put("o",order);
        map.put("reviews",reviews);

        return Result.success(map);

    }

    @PostMapping("foredoreview")
    public Object doreview(HttpSession session, int oid, int pid, String content) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.finish);
        orderService.update(order);

        Product product = productService.get(pid);
        content = HtmlUtils.htmlEscape(content);

        User user = (User) session.getAttribute("user");
        Review review = new Review();
        review.setContent(content);
        review.setProduct(product);
        review.setCreateDate(new Date());
        review.setUser(user);
        reviewService.add(review);
        return Result.success();
    }

}
