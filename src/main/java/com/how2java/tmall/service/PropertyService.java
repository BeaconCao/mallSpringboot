package com.how2java.tmall.service;

import com.how2java.tmall.dao.PropertyDao;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Property;
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

import java.util.List;

@Service
@CacheConfig(cacheNames = "properties")
public class PropertyService {
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private CategoryService categoryService;
    @CacheEvict(allEntries = true)
    public void add(Property property) {
        propertyDao.save(property);
    }
    @CacheEvict(allEntries = true)
    public void delete(int id) {
        propertyDao.delete(id);
    }
    @CacheEvict(allEntries = true)
    public void update(Property property) {
        propertyDao.save(property);
    }
    @Cacheable(key = "'properties-one-'+#p0")
    public Property get(int id) {
        return  propertyDao.getOne(id);
    }
    @Cacheable(key = "'properties-cid-'+#p0+'-page-'+#p1 + '-' + #p2")
    public Page4Navigator<Property> list(int cid, int start, int size, int navigatePages) {
        Category category = categoryService.get(cid);
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        Pageable pageable = new PageRequest(start,size,sort);
        Page<Property>  pageFromJpa   =propertyDao.findByCategory(category,pageable);
        return new Page4Navigator<>(pageFromJpa,navigatePages);
    }
    @Cacheable(key = "'properties-cid-'+#p0.id")
    public List<Property> listByCategory(Category category) {
        return  propertyDao.findByCategory(category);
    }

}
