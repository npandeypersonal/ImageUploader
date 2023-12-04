package com.assesment.imageuploader.di.module

import com.assesment.imageuploader.dataModel.network.ApiService
import com.assesment.imageuploader.dataModel.repository.ImageRepository
import com.assesment.imageuploader.utils.Constants
import com.assesment.imageuploader.viewModel.ImageViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providesUrl() = "https://api.bytescale.com/v2/accounts/kW15boc/"

    @Provides
    fun provideImageViewModel(repository: ImageRepository): ImageViewModel{
       return ImageViewModel(imageRepository = repository)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
// set your desired log level
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(Interceptor {
                val request = it.request().newBuilder()
                    .addHeader("Authorization", Constants.authHeader)
                    .build();
                it.proceed(request)
            })
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun providesApiService(url:String, okHttpClient: OkHttpClient) : ApiService =
        Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
}