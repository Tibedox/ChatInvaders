package ru.tibedox.chatinvaders;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MyApi {
    @GET("/chatinvaders.php")
    Call<List<DataFromDB>> sendToServer(@Query("q") String s);

    @GET("/chatinvaders.php")
    Call<List<DataFromDB>> sendToServer(@Query("name") String name, @Query("message") String message);
}
