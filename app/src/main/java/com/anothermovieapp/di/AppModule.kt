package com.anothermovieapp.di

import android.content.Context
import androidx.room.Room
import com.anothermovieapp.movieList.ViewModelMovieDetails
import com.anothermovieapp.movieList.ViewModelPopularMovies
import com.anothermovieapp.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import javax.inject.Singleton
import com.anothermovieapp.movieList.ViewModelTopRatedMovies
import okhttp3.Cache
import java.io.File

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideViewModelMovieDetails(
        r: RepositoryMovies
    ): ViewModelMovieDetails {
        return ViewModelMovieDetails(r)
    }

    @Singleton
    @Provides
    fun provideRepositoryMoviesImpl(
        db: Database,
        moviesWebService: WebserviceMovieDatabase
    ): RepositoryMovies {
        return RepositoryMoviesImpl(db, moviesWebService)
    }

    @Singleton
    @Provides
    fun provideViewModelTopRatedMovies(
        r: RepositoryTopRatedList
    ): ViewModelTopRatedMovies {
        return ViewModelTopRatedMovies(r)
    }

    @Singleton
    @Provides
    fun provideRepositoryTopRatedList(
        db: Database,
        moviesWebService: WebserviceMovieDatabase
    ): RepositoryTopRatedList {
        return RepositoryTopRatedListImpl(db, moviesWebService)
    }

    @Singleton
    @Provides
    fun provideViewModelPopularMovies(
        r: RepositoryPopularList
    ): ViewModelPopularMovies {
        return ViewModelPopularMovies(r)
    }

    @Singleton
    @Provides
    fun provideRepositoryPopularList(
        db: Database,
        moviesWebService: WebserviceMovieDatabase
    ): RepositoryPopularList {
        return RepositoryPopularListImpl(db, moviesWebService)
    }

    @Singleton
    @Provides
    fun provideWebService(okHttpClient: OkHttpClient): WebserviceMovieDatabase {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org")
            .client(okHttpClient)
            .build()
        return retrofit.create(WebserviceMovieDatabase::class.java)
    }

    @Singleton
    @Provides
    fun provideWebServiceTrailers(okHttpClient: OkHttpClient): WebserviceTrailers {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org")
            .client(okHttpClient)
            .build()
        return retrofit.create(WebserviceTrailers::class.java)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val interceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val originalResponse = chain.proceed(chain.request())
                return if (false) {
                    val maxAge = 60 // read from cache for 1 minute
                    originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=$maxAge")
                        .build()
                } else {
                    val maxStale = 60 * 60 * 24 * 28 // tolerate 4-weeks stale
                    originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                        .build()
                }
            }
        }
        //setup cache
        val httpCacheDirectory = File(context.getCacheDir(), "responses")
        val cacheSize : Long = 10 * 1024 * 1024 // 10 MiB

        val cache = Cache(httpCacheDirectory, cacheSize)

        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(interceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(
            context.applicationContext, Database::class.java, "movies_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

}