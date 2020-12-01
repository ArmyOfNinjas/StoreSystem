package com.repository;

import com.entity.Product;
import com.entity.Store;

import java.util.Collection;

public interface StoreRepository {
        public Store save(Store store);
        public boolean delete(int id);
        public Store get(int id);
        public Collection<Store> getAll();
        public int saveProduct(int storeId, int productId, float price);
        public int updateProduct(int storeId, int productId, float price);
        public Product getProduct(int storeId, int productId, float price);
        public int getProductId(int storeId, int productId, float price);
        public boolean removeProduct(int productId);
        public Collection<Product> getAllProducts(int storeId);
}
