package com.golub.golubroman.innotechtest.Start.Retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

//  creating interface for communicating with server
public interface RetrofitInterface {

//    method for post request to server
    @FormUrlEncoded
    @POST("/testdata/data.php")
    Call<String> getCode(@Field("data") String data);
}
