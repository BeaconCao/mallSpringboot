package com.how2java.tmall.service;

import com.how2java.tmall.dao.ReviewDao;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "reviews")
public class ReviewService {

    @Autowired
    private ReviewDao reviewDao;

    @Cacheable(key ="'reviews-pid-'+#p0.id")
    public List<Review> list(Product product) {
        List<Review> bean = reviewDao.findByProductOrderByIdDesc(product);
        return bean;
    }

    @Cacheable(key = "'reviews-count-pid-'+#p0.id" )
    public int getCount(Product product) {
        int count = reviewDao.countByProduct(product);
        return count;
    }

    @CacheEvict(allEntries = true)
    public void add(Review review) {
        reviewDao.save(review);
    }
}
