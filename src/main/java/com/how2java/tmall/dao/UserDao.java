package com.how2java.tmall.dao;

import com.how2java.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User, Integer> {
    User getByName(String name);
    User getByNameAndPassword(String name,String password);

}
