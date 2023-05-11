package com.company;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import  java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FoodProductTest {
    @Test
    void nameTest(){
        String expectedName="Buraki - za 1 kg";
        FoodProduct tempProduct = FoodProduct.fromCsv(Path.of("/Users/urszulawrobel/Downloads/programowanie_obiektowe_grupa1-main/src/test/resources/buraki.csv"));
        assertEquals(expectedName,tempProduct.getName());
    }

    @Test
    void priceTest(){
        int year = 2010;
        int month = 3;
        String province = "DOLNOŚLĄSKIE";
        double expectedPrice = 1.57;
        FoodProduct tempProduct = FoodProduct.fromCsv(Path.of("/Users/urszulawrobel/Downloads/programowanie_obiektowe_grupa1-main/src/test/resources/buraki.csv"));
        assertEquals(expectedPrice,tempProduct.getPrice(year,month,province));
    }
    @Test
    void priceTest2() throws NoSuchFieldException, IllegalAccessException {
        int year = 2010;
        int month = 3;
        String province = "DOLNOŚLĄSKIE";
        double expectedPrice = 1.57;
        FoodProduct tempProduct = FoodProduct.fromCsv(Path.of("C:\\Users\\student\\Documents\\lk\\programowanie_obiektowe_grupa1\\src\\test\\resources\\buraki.csv"));

        Field pricesField = FoodProduct.class.getDeclaredField("prices");
        pricesField.setAccessible(true);
        Map<String, Double[]> prices = (Map<String, Double[]>) pricesField.get(tempProduct);

        assertEquals(expectedPrice, prices.get(province)[Product.priceIndex(year, month)]);
    }
    @Test
    void fromCsvName() {
        FoodProduct product = FoodProduct.fromCsv(Path.of("test/resources/testfoodproduct.csv"));
        assertEquals("produkt testowy", product.getName());
    }

    @Test
    void fromCsvPrice() throws NoSuchFieldException, IllegalAccessException {
        FoodProduct product = FoodProduct.fromCsv(Path.of("test/resources/testfoodproduct.csv"));

        // dostęp do prywatnego pola - zła praktyka
        Field pricesField = FoodProduct.class.getDeclaredField("prices");
        pricesField.setAccessible(true);
        Map<String, Double[]> prices = (Map<String, Double[]>) pricesField.get(product);

        assertEquals(6.0, prices.get("yyy")[2]);
    }

    @Test
    void fromCsvPrice2() {
        FoodProduct product = FoodProduct.fromCsv(Path.of("test/resources/testfoodproduct.csv"));
        FoodProductExposed exposedProduct = new FoodProductExposed(product);

        assertEquals(6.0, exposedProduct.getPrices().get("yyy")[2]);
    }

    @Test
    void fromCsvPrice3() {
        FoodProduct product = FoodProduct.fromCsv(Path.of("test/resources/testfoodproduct.csv"));

        assertEquals(6.0, product.getPrice(2010, 3, "yyy"));
    }

}