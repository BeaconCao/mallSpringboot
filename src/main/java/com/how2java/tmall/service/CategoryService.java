package com.how2java.tmall.service;

import com.how2java.tmall.dao.CategoryDao;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@CacheConfig(cacheNames = "categories")
public class CategoryService {
    @Autowired
    CategoryDao categoryDAO;


    @Cacheable(key = "'categories-all'")
    public List<Category> list() {
        Sort sort = new Sort(Sort.Direction.DESC, "id");

        return categoryDAO.findAll(sort);
    }
    @Cacheable(key = "'categories-page-' + #p0 + '-' + #p1")
    public Page4Navigator<Category> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page pageFromJpa = categoryDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJpa, navigatePages);
    }

    @CacheEvict(allEntries = true)
    public void add(Category category) {

        categoryDAO.save(category);
    }
    @CacheEvict(allEntries = true)
    public void delete(int id) {
        categoryDAO.delete(id);
    }
    @Cacheable(key = "'categories-ome-'+#p0")
    public Category get(int id) {
        return categoryDAO.findOne(id);
    }
    @CacheEvict(allEntries = true)
    public void update(Category bean) {
        categoryDAO.save(bean);
    }

    public void removeCategoryFromProduct(Category category) {
        List<Product> products = category.getProducts();
        if (null != products) {
            for (Product product : products) {
                product.setCategory(null);
            }
        }
        List<List<Product>> productsByRow = category.getProductsByRow();
        if (null != productsByRow) {
            for (List<Product> ps : productsByRow) {
                for (Product p : ps) {
                    p.setCategory(null);
                }
            }
        }
    }

    public void removeCategoryFromProduct(List<Category> categorys) {
        for (Category category : categorys) {
            removeCategoryFromProduct(category);
        }
    }



}
