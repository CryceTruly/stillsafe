package com.crycetruly.a4app.remote;

/**
 * Created by Elia on 3/11/2018.
 */

public class Common {
    public static String baseUrl="https://googleapis.com";
    public static iGoogleApi getGoogleApi(){
        return RetrofitClient.getRetrofitClient(baseUrl).create(iGoogleApi.class);

    }

}
