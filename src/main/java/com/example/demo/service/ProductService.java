package com.example.demo.service;

import com.example.demo.model.Product;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

@Service
public class ProductService {

    private static final String PRODUCT_FILE_NAME = "products.json";

    public Map<String, Product> getProducts() throws FileNotFoundException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type type = new TypeToken<Map<String, Product>>() {
        }.getType();
        Reader reader = new FileReader(PRODUCT_FILE_NAME);
        return gson.fromJson(reader, type);
    }

    public void createProduct(Product product) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, Product> products = this.getProducts();
        products.put(product.getId(), product);
        Writer writer = new FileWriter(PRODUCT_FILE_NAME);
        gson.toJson(products, writer);
        writer.close();
    }

    public void updateProduct(Map<String, Product> products) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Writer writer = new FileWriter(PRODUCT_FILE_NAME);
        gson.toJson(products, writer);
        writer.close();
    }

    public Map<String, Product> sortByValue(Map<String, Product> products) {
        List<Map.Entry<String, Product>> list =
                new LinkedList<Map.Entry<String, Product>>(products.entrySet());

        list.sort(new Comparator<Map.Entry<String, Product>>() {
            public int compare(Map.Entry<String, Product> o1,
                               Map.Entry<String, Product> o2) {
                return (o2.getValue().getVisitedCount()) - (o1.getValue().getVisitedCount());
            }
        });

        Map<String, Product> sortedMap = new LinkedHashMap<String, Product>();
        for (Map.Entry<String, Product> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public Map<String, Product> limitProducts(Map<String, Product> products, int limit) {
        Map<String, Product> limitedProducts = new LinkedHashMap<>();
        for (Map.Entry<String, Product> entry : products.entrySet()) {
            limitedProducts.put(entry.getKey(), entry.getValue());
            limit--;
            if (limit == 0) break;
        }
        return limitedProducts;
    }
}
