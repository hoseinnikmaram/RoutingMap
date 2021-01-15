package com.nikmaram.map.Network;


import com.nikmaram.map.model.AddressModel;
import com.nikmaram.map.model.RoutesModel;

import io.reactivex.Single;
import retrofit2.Retrofit.Builder;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface Api {
    Api.Companion Companion= Api.Companion.$$Instance;

    @Headers({"Api-Key: service.pmOdJ1OxRZoQBHyMFjWqKEFsIsWhomC5xLFK2Abz"})
    @GET("direction")
    Single<RoutesModel> Get_Routes(
            @Query("origin") String origin,
            @Query("destination") String destination
    );

    @Headers({"Api-Key: service.pmOdJ1OxRZoQBHyMFjWqKEFsIsWhomC5xLFK2Abz"})
    @GET("reverse")
    Single<AddressModel> Get_Address(
            @Query("lat") String lat,
            @Query("lng") String lng
    );


    public static final class Companion{
     static final Api.Companion $$Instance;
     public final Api invoke(){
         Object object=(new Builder())
                 .baseUrl("https://api.neshan.org/v2/")
                 .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                 .addConverterFactory(GsonConverterFactory.create())
                 .build()
                 .create(Api.class);
         return (Api)object;
     }
     static {
         Api.Companion api = new Companion();
         $$Instance=api;
     }
    }
}
