package com.how2java.tmall.service;

import com.how2java.tmall.dao.UserDao;
import com.how2java.tmall.pojo.User;
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

@Service
@CacheConfig(cacheNames = "users")
public class UserService {
    @Autowired
    private UserDao userDao;
    @Cacheable(key = "'users-page-'+#p0+'-'+#p1")
    public Page4Navigator<User> lsit(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page<User> pageFromJpa = userDao.findAll(pageable);
        return new Page4Navigator<>(pageFromJpa,navigatePages);
    }

    public boolean isExist(String name) {
        User bean = userDao.getByName(name);
        return null != bean;
    }

    @CacheEvict(allEntries = true)
    public void add(User user) {

        userDao.save(user);
    }
    @Cacheable(key = "'users-one-name-'+#p0+'-password-'+#p1")
    public User getByNameAndPassword(String name,String password) {
        User bean = userDao.getByNameAndPassword(name, password);
        return bean;
    }
    @Cacheable(key = "'users-one-name-'+#p0")
    public User getByName(String name) {
        return  userDao.getByName(name);
    }
}
