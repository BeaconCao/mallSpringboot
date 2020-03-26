package com.how2java.tmall.comparator;

import com.how2java.tmall.pojo.Product;

import java.util.Comparator;

public class ProductComparator implements Comparator<Product> {
    private String sort;
    public ProductComparator(String sort) {
        this.sort = sort ;
    }

    public ProductComparator() {
    }

    @Override
    public int compare(Product o1, Product o2) {
        int result = 0 ;
        switch (sort) {
            case "review":
                result = o1.getReviewCount()-o2.getReviewCount();
                break;
            case "date":
                result = o1.getCreateDate().compareTo(o2.getCreateDate());
                break;
            case "saleCount":
                result = o1.getSaleCount() - o2.getSaleCount();
                break;
            case "price":
                result = (int) (o1.getPromotePrice()-o2.getPromotePrice());
                break;
            case "all":
                result = o1.getSaleCount()*o1.getReviewCount()-o2.getSaleCount()*o2.getReviewCount();
                break;
            default:
                result = 0 ;
                break;
        }
        return  result;

    }
}
