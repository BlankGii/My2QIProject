package com.example.myretrofittest.network;

import com.example.myretrofittest.network.bean.HeadNewResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by gcy on 2017/2/9 0009.
 */
public interface Api {
    @GET("0-20.html")
    Observable<HeadNewResponse> getNewsLists();
}
