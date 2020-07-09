package com.repository;

import com.entity.Product;

import java.util.Collection;

public interface ProductRepository {
        public Product save(Product product);
        public boolean delete(int id);
        public Product get(int id);
        public Collection<Product> getAll();
}
