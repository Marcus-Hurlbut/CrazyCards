package com.dungeondecks.marcushurlbut.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.dungeondecks.marcushurlbut.games.card.Card;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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

    public static List<Card> cardFromJSON(String str) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Card> cards = objectMapper.readValue(str, new TypeReference<List<Card>>(){});
            return cards;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new ArrayList<Card>();
    }



}