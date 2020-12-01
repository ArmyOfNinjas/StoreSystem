package com.entity;

import junit.framework.TestCase;

public class ProductTest extends TestCase {
    private final Product TEST_PRODUCT = new Product(3,"123","TEST");

    public void testGetId() {
        assertEquals((int)TEST_PRODUCT.getId(),3 );
    }

    public void testGetBarCode() {
        assertEquals(TEST_PRODUCT.getBarCode(), "123");
    }

    public void testGetProductName() {
        assertEquals(TEST_PRODUCT.getProductName(), "TEST");
    }
}