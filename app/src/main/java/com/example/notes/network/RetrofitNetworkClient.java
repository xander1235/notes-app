package com.example.notes.network;

import com.example.notes.pojos.requests.ReqTopic;
import com.example.notes.pojos.requests.ReqUser;
import com.example.notes.pojos.responses.ResLogin;
import com.example.notes.pojos.responses.ResLogout;
import com.example.notes.pojos.responses.ResTopic;
import com.example.notes.pojos.responses.ResTopicsList;
import com.example.notes.pojos.responses.ResUser;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitNetworkClient {

    @POST("/user")
    Call<ResUser> createUser(@Body ReqUser reqUser);

    @GET("/user/login/{user_name}")
    Call<ResLogin> userLogin(@Path("user_name") String username, @Query("password") String password);

    @POST("/notes")
    Call<ResTopic> createTopic(@Body ReqTopic resTopic, @Header("transaction_id") String transactionId);

    @GET("/notes")
    Call<ResTopicsList> getAllNotes(@Query("page") int page, @Query("size") int size, @Header("transaction_id") String transactionId);

    @PUT("/notes")
    Call<ResTopic> updateNote(@Body ReqTopic reqTopic, @Header("transaction_id") String transactionId);

    @DELETE("/notes/{title}")
    Call<ResLogout> deleteNote(@Header("transaction_id") String transactionId, @Path("title") String title);
}
