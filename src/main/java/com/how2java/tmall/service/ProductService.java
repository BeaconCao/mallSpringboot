package com.how2java.tmall.service;

import com.how2java.tmall.dao.ProductDao;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.util.Page4Navigator;
import com.how2java.tmall.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames = "products")
public class ProductService {



    @Autowired
    private ProductDao productDao;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductImageService productImageService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ReviewService reviewService;


    @Cacheable(key = "'products-cid-'+#p0+'-page-'+#p1+'-'+#p2")
    public Page4Navigator<Product> list(int cid, int start, int size, int navigatePages) {
        Category category = categoryService.get(cid);

        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page<Product> pageFromJPA = productDao.findByCategory(category, pageable);
        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }
    @Cacheable(key = "'products-one' + #p0")
    public Product get(int id) {
        return productDao.findOne(id);
    }
    @Cacheable(key = "'products-cid-'+#p0.id")
    public List<Product> listByCategory(Category category) {
        return productDao.findByCategoryOrderById(category);
    }



    @CacheEvict(allEntries = true)
    public void add(Product bean) {
        productDao.save(bean);

    }
    @CacheEvict(allEntries = true)
    public void delete(int id) {
        productDao.delete(id);
    }
    @CacheEvict(allEntries = true)
    public void update(Product bean) {
        productDao.save(bean);
    }



    public void fill(Category category) {
        ProductService productService = SpringContextUtil.getBean(ProductService.class);
        List<Product> products = productService.listByCategory(category);
        productImageService.setFirstProductImage(products);
        category.setProducts(products);
    }

    public void fill(List<Category> categories) {
        for (Category category : categories) {
            fill(category);
        }
    }

    public void fillByRow(List<Category> categories) {
        int productNumberEachRow = 8 ;
        for (Category category : categories) {
            List<Product> products = category.getProducts();
            ArrayList<List<Product>> productsByRow = new ArrayList<>();
            for (int i = 1; i < products.size(); i += productNumberEachRow) {
                int size = i+productNumberEachRow;
                size = size>products.size()?products.size():size;
                List<Product> productsOfEachRow = products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }

    }

    public void setSaleAndReviewNumber(Product product) {
        int salesCount = orderItemService.getSalesCount(product);
        product.setSaleCount(salesCount);

        int reviewsCount = reviewService.getCount(product);
        product.setReviewCount(reviewsCount);
    }

    public void setSaleAndReviewNumber(List<Product> products) {
        for (Product product : products)
            setSaleAndReviewNumber(product);
    }

    public List<Product> search(int start, int size, String keyword) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = (Pageable) new PageRequest(start, size, sort);

        List<Product> products = productDao.findByNameLike("%" + keyword + "%", pageable);
        return products;
    }

}

