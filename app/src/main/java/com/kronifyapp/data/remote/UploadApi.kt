package com.kronifyapp.data.remote

import com.kronifyapp.data.remote.response.UploadResponse
import okhttp3.MultipartBody
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadApi {
    @Multipart
    @POST("upload")
    suspend fun upload(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
    ): UploadResponse
}
