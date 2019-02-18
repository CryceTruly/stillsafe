package com.crycetruly.a4app.remote;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Elia on 3/11/2018.
 */

public class RetrofitClient {
    private static Retrofit retrofit=null;
    public static Retrofit getRetrofitClient(String baseUrl){

        if (retrofit==null){
            retrofit=new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(ScalarsConverterFactory.create()).build();
        }
        return retrofit;
    }
}
