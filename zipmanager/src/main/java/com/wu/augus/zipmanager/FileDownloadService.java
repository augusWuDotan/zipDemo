package com.wu.augus.zipmanager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface FileDownloadService {

    @GET
    Call<ResponseBody> LoadFile(@Url String Url);

    @GET("/{fileName}")
    Call<ResponseBody> LoadFileName(@Path("fileName") String fileName);

}
