package com.example.myecommerce.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.myecommerce.BuildConfig
import com.example.myecommerce.data.repository.auth.AuthRepository
import com.example.myecommerce.data.repository.auth.AuthRepositoryImp
import com.example.myecommerce.data.api.*
import com.example.myecommerce.data.api.service.ApiProduct
import com.example.myecommerce.data.api.service.ApiService
import com.example.myecommerce.data.local.room.ProductDao
import com.example.myecommerce.data.model.UserPreference
import com.example.myecommerce.data.model.dataStore
import com.example.myecommerce.data.repository.product.ProductRepository
import com.example.myecommerce.data.repository.product.ProductRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        return if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        headerInterceptor: HeaderInterceptor,
        authExpiredToken: AuthExpiredAuthentication,
        authAuthentication: AuthAuthentication,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(headerInterceptor)
            .addInterceptor(authExpiredToken)
            .authenticator(authAuthentication)
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun providesAuthAuthentication(pref: UserPreference): AuthAuthentication =
        AuthAuthentication(pref)

    @Singleton
    @Provides
    fun providesAuthInterceptor(pref: UserPreference, @ApplicationContext context: Context): AuthExpiredAuthentication =
        AuthExpiredAuthentication(pref, context)

    @Singleton
    @Provides
    fun providesHeaderInterceptor(pref: UserPreference): HeaderInterceptor = HeaderInterceptor(pref)

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Singleton
    @Provides
    fun provideRetrofitBuilder(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://portlan.id/training_android/public/api/ecommerce/")
//            .baseUrl("http://172.17.20.201/training_android/public/api/ecommerce/")
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideApiProduct(retrofit: Retrofit): ApiProduct {
        return retrofit.create(ApiProduct::class.java)
    }

    @Singleton
    @Provides
    fun providesAuthRepository(apiService: ApiService): AuthRepository {
        return AuthRepositoryImp(apiService)
    }

    @Singleton
    @Provides
    fun providesProductRepository(apiProduct: ApiProduct, productDao: ProductDao): ProductRepository {
        return ProductRepositoryImp(apiProduct, productDao)
    }

    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore


    @Provides
    fun provideUserPreference(dataStore: DataStore<Preferences>) : UserPreference = UserPreference(dataStore)

}