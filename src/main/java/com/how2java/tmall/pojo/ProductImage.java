package com.how2java.tmall.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.junit.ClassRule;

import javax.persistence.*;

@Entity
@Table(name = "productimage")
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
public class ProductImage {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "pid")
    @ManyToOne
    @JsonBackReference
    private Product product;

    @Column(name = "type")
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
