package com.how2java.tmall.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "user")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name ;
    private String password;
    private String salt;

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", anonymousName='" + anonymousName + '\'' +
                '}';
    }

    @Transient
    private String anonymousName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAnonymousName() {
        if (null != anonymousName) {
            return anonymousName;
        }
        if (null == name) {
            anonymousName = null;
        } else if (name.length() <= 1) {
            anonymousName = "*";
        } else if (name.length() == 2) {
            anonymousName = name.substring(0,1)+"*";
        } else {
            char[] name = this.name.toCharArray();
            for (int i = 1 ; i < name.length - 1; i++) {
                name[i]='*';
            }
            String s = new String(name);
            anonymousName = s;
        }
        return anonymousName;
    }

    public void setAnonymousName(String anonymousName) {
        this.anonymousName = anonymousName;
    }


}
