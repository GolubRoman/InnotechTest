package com.golub.golubroman.innotechtest.Start.Retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


//  creating retrofitModule
public class RetrofitModule {

    private static Retrofit retrofit;
//    object of retrofitInterface
    private static RetrofitInterface retrofitInterface;

//    building retrofit module and returning retrofitInterface
    public static RetrofitInterface buildApi(){

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

//        building retrofit object
        retrofit = new Retrofit.Builder().
                baseUrl("http://s3.logist.ua/").
                addConverterFactory(new StringConverter()).
                build();

//        building retrofit interface and returning that one
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        return retrofitInterface;
    }
}
