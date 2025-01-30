package com.dungeondecks.marcushurlbut.utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
    
    public static String toJSON(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonArray = objectMapper.writeValueAsString(obj);
            return jsonArray;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    public static List<Integer> intListFromJSON(String str) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> strList = objectMapper.readValue(str, List.class);
            List<Integer> intList = strList.stream().map(Integer::parseInt).collect(Collectors.toList());
            return intList;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}