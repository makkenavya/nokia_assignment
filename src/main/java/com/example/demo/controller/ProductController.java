package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.model.ProductResponse;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping(value = {"", "/"})
    public ResponseEntity<Object> create(@Valid @RequestBody Product product) {
        try {
            productService.createProduct(product);
            return ProductResponse.generateResponse("Product Created successfully", HttpStatus.OK, product, null);
        } catch (FileNotFoundException e) {
            return ProductResponse.generateResponse("Unable to locate file", HttpStatus.BAD_REQUEST, null, e.getMessage());
        } catch (Exception e) {
            return ProductResponse.generateResponse("Product Creation failed", HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }

    @RequestMapping("/id/{id}")
    public ResponseEntity<Object> get(@PathVariable String id) {
        try {
            Map<String, Product> products = productService.getProducts();
            if (products.get(id) == null) {
                return ProductResponse.generateResponse("Unable to find product", HttpStatus.BAD_REQUEST, null, null);
            }
            products.get(id).setVisitedCount(products.get(id).getVisitedCount() + 1);
            productService.updateProduct(products);
            return ProductResponse.generateResponse("Product retrieved", HttpStatus.OK, products.get(id), null);
        } catch (FileNotFoundException e) {
            return ProductResponse.generateResponse("Unable to locate file", HttpStatus.BAD_REQUEST, null, e.getMessage());
        } catch (Exception e) {
            return ProductResponse.generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }

    @RequestMapping(value = {"/popular", "/popular/{limit}"})
    public ResponseEntity<Object> getPopularProducts(@PathVariable(required = false) Integer limit) {
        try {
            if (limit == null) limit = 5;
            Map<String, Product> products = productService.getProducts();
            Map<String, Product> popularProducts = new HashMap<>();
            for (Map.Entry<String, Product> entry : products.entrySet()) {
                if (entry.getValue().getVisitedCount() >= 1) {
                    popularProducts.put(entry.getKey(), entry.getValue());
                }
            }
            Map<String, Product> sortedProducts = productService.sortByValue(popularProducts);
            Map<String, Product> limitProducts = productService.limitProducts(sortedProducts, limit);
            return ProductResponse.generateResponse("Top " + limitProducts.size() + " popular products", HttpStatus.OK, limitProducts, null);
        } catch (FileNotFoundException e) {
            return ProductResponse.generateResponse("Unable to locate file", HttpStatus.BAD_REQUEST, null, e.getMessage());
        } catch (Exception e) {
            return ProductResponse.generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ProductResponse.generateResponse("Required fields are missing", HttpStatus.BAD_REQUEST, null, errors);
    }

}