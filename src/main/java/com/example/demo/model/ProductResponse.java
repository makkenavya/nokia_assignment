package com.example.demo.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ProductResponse {
    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj, Object errorObj) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", message);
        map.put("status", status.value());
        if (responseObj != null) map.put("data", responseObj);
        if (errorObj != null) map.put("errorDetails", errorObj);
        return new ResponseEntity<Object>(map, status);
    }
}
