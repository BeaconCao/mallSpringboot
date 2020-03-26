package com.how2java.tmall.service;

import com.how2java.tmall.dao.PropertyValueDao;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.pojo.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "propertyValues")
public class PropertyValueService {
    @Autowired
    private PropertyValueDao propertyValueDao;
    @Autowired
    private PropertyService propertyService;

    @CacheEvict(allEntries = true)
    public void update(PropertyValue bean) {
        propertyValueDao.save(bean);
    }
    @Cacheable(key = "'propertyValues-pid-'+#p0.id+'-ptid-'+'#p1.id'")
    public PropertyValue getByProductAndProperty(Product product, Property property) {
        PropertyValue bean = propertyValueDao.getByPropertyAndProduct(property, product);
        return bean;
    }
    @Cacheable(key = "'propertyValues-pid-'+#p0.id")
    public List<PropertyValue> list(Product product) {
        List<PropertyValue> bean = propertyValueDao.findByProductOrderByIdDesc(product);
        return bean;
    }

    public void init(Product product) {
        List<Property> properties = propertyService.listByCategory(product.getCategory());
        for (Property property : properties) {
            PropertyValue propertyValue = getByProductAndProperty(product, property);
            if (null == propertyValue) {
                PropertyValue bean = new PropertyValue();
                bean.setProduct(product);
                bean.setProperty(property);
                propertyValueDao.save(bean);
            }
        }
    }
}
