package com;


import org.json.JSONArray;

import org.json.JSONObject;

public class JSONParser {


    private static String json;

    public JSONParser(String json) {
        this.json = json;
    }



    public void parse() {
        JSONObject obj = new JSONObject(json);
    }






}
