package com.example.velveteyebrows.services;

public class HttpHelper {
    public static boolean isRequestSuccesful(int responseCode){
        return responseCode >= 200 && responseCode <= 299;
    }
}
