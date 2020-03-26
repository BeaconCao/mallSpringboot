package com.how2java.tmall.util;

import org.springframework.data.domain.Page;

import java.util.List;

public class Page4Navigator<T> {
    Page<T> pageFromJPA;
    int navigatePages;
    int totalPages;
    int number;
    long totalElements;
    int size;
    int numberOfElements;
    List<T> content;

    boolean isHasContent;
    boolean first;
    boolean last;
    boolean isHasNext;
    boolean isHasPrevious;
    int[] navigatepageNums;
    //无参构造方法
    public Page4Navigator() {
        //
    }
    //包含两个参数的构造方法
    public Page4Navigator(Page<T> pageFromJPA, int navigatePages) {
        this.pageFromJPA = pageFromJPA;
        this.navigatePages = navigatePages;
        this.totalPages=pageFromJPA.getTotalPages();
        this.number=pageFromJPA.getNumber();
        this.totalElements=pageFromJPA.getTotalElements();
        this.size=pageFromJPA.getSize();
        this.numberOfElements=pageFromJPA.getNumberOfElements();
        this.content=pageFromJPA.getContent();

        this.isHasContent=pageFromJPA.hasContent();
        this.first=pageFromJPA.isFirst();
        this.last=pageFromJPA.isLast();
        this.isHasNext=pageFromJPA.hasNext();
        this.isHasPrevious=pageFromJPA.hasPrevious();
        caclNavigatepageNums();
    }
    //方法：计算导航页数。
    public void caclNavigatepageNums() {
        //数据准备
        int[] navigatepageNums;
        int totalPages = getTotalPages();
        int num = getNumber();

        //当总页数小于或等于导航页码数时
        if (totalPages <= navigatePages) {
            navigatepageNums = new int[totalPages];
            for (int i = 0; i < totalPages; i++) {
                navigatepageNums[i] = i + 1;
            }
        } else {
            //当页码总数大于导航页数时
            //当前页处在一个什么位置，经过思考我们可以确认，有三种可能，一种是处在最前，一种是处在最后，还有一种是处在中间。
            navigatepageNums=new int[navigatePages];
            int startNum = num-navigatePages/2;
            int endNum = num + navigatePages/2;
            //页码处在最前
            if (startNum < 1) {
                startNum=1;
                for (int i = 0; i < navigatePages; i++) {
                    navigatepageNums[i] = startNum;
                    startNum++;
                }

            } else if (endNum > totalPages) {
                //页码处在最后
                endNum = totalPages;
                for (int i = navigatePages - 1; i >= 0; i--) {
                    navigatepageNums[i] = endNum;
                    endNum--;
                }
            } else {
             //页码处在中间位置。
                for (int i = 0; i < navigatePages; i++) {
                    navigatepageNums[i]= startNum;
                    startNum++;
                }
            }
        }

        this.navigatepageNums = navigatepageNums;
    }

    //



    public int getNavigatePages() {
        return navigatePages;
    }

    public void setNavigatePages(int navigatePages) {
        this.navigatePages = navigatePages;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public boolean isHasContent() {
        return isHasContent;
    }

    public void setHasContent(boolean hasContent) {
        isHasContent = hasContent;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isHasNext() {
        return isHasNext;
    }

    public void setHasNext(boolean hasNext) {
        isHasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return isHasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        isHasPrevious = hasPrevious;
    }

    public int[] getNavigatepageNums() {
        return navigatepageNums;
    }

    public void setNavigatepageNums(int[] navigatepageNums) {
        this.navigatepageNums = navigatepageNums;
    }
}
