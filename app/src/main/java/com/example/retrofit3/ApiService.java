package com.example.retrofit3;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @Headers("Authorization: Client-ID 92584a8d6aafd74")
    @POST("3/image")
    Call <ResponseDTO> uploadImage(
            @Part MultipartBody.Part image
            );

}
