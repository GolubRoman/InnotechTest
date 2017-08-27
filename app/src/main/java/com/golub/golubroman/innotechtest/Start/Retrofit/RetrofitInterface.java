package com.golub.golubroman.innotechtest.Start.Retrofit;

import retrofit2.Call;
import retrofit2.http.POST;

//  creating interface for communicating with server
public interface RetrofitInterface {

//    method for post request to server
    @POST("testdata/data.php")
    Call<String> getCode();
}
