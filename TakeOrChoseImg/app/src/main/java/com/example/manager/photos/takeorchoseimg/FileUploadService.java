package com.example.manager.photos.takeorchoseimg;

import com.squareup.okhttp.RequestBody;

import java.util.Map;

import retrofit.Call;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.PartMap;

public interface FileUploadService {

    @Multipart
    @POST("files")
    Call<FileRepresentation> upload(
            //@Part("uploadFile\"; filename=\"image.png\" ") RequestBody file);
            @PartMap Map<String, RequestBody> map);
}