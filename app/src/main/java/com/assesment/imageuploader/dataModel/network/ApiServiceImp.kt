package com.assesment.imageuploader.dataModel.network

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class ApiServiceImp @Inject constructor(private val apiService: ApiService)  {
    suspend fun uploadImage(requestBody: RequestBody): Response<JsonObject> {
        return apiService.uploadImage(requestBody)
    }
}