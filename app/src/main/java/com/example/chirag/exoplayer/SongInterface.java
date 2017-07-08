package com.example.chirag.exoplayer;

import com.example.chirag.exoplayer.model.MainResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by chirag on 07-Jul-17.
 */

public interface SongInterface {

    @GET("song/all")
    Call<MainResponse> MAIN_RESPONSE_CALL();
}
