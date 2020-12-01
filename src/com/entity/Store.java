package com.entity;

import java.util.List;

/**
 * Class represents a physical store with it's address, name and list of products that are sold in it.
 */
public class Store {
    private Integer id;
    private String name;
    private String address;
    private List<Product> productList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public Store(Integer id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public boolean isNew() {
        return id==null || id==0;
    }

    @Override
    public String toString() {
        return name + " - " +  address;
    }
}
