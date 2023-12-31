package com.assesment.imageuploader.dataModel.network

import com.assesment.imageuploader.utils.Constants
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiService{
    @POST("uploads/binary")
    suspend fun uploadImage(@Body requestBody: RequestBody): Response<JsonObject>
}