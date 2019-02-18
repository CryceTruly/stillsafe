package com.crycetruly.a4app.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Elia on 3/11/2018.
 */

public interface iGoogleApi {

    @GET
    Call<String> getDataFromGoogleApi(@Url String url);
}
