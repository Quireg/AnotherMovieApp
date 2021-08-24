/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.repository

import com.anothermovieapp.BuildConfig
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WebserviceMovieDatabase {

    @GET("/3/movie/{id}/")
    suspend fun getMovie(
        @Path("id") id: String,
        @Query("api_key") key: String = BuildConfig.MOVIE_DATABASE_API_KEY
    ): ResponseBody

    @GET("/3/movie/{id}/videos")
    suspend fun getMovieTrailers(
        @Path("id") id: String,
        @Query("api_key") key: String = BuildConfig.MOVIE_DATABASE_API_KEY
    ): ResponseBody

    @GET("/3/movie/{id}/reviews/")
    suspend fun getMovieReviews(
        @Path("id") id: String,
        @Query("api_key") key: String = BuildConfig.MOVIE_DATABASE_API_KEY
    ): ResponseBody

    @GET("/3/movie/{id}/reviews")
    suspend fun getMovieReviewsForPage(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("api_key") key: String = BuildConfig.MOVIE_DATABASE_API_KEY
    ): ResponseBody

    @GET("/3/movie/popular/")
    suspend fun getMoviesListPopular(
        @Query("page") page: Int,
        @Query("api_key") key: String = BuildConfig.MOVIE_DATABASE_API_KEY
    ): ResponseBody

    @GET("/3/movie/top_rated/")
    suspend fun getMoviesListTopRated(
        @Query("page") page: Int,
        @Query("api_key") key: String = BuildConfig.MOVIE_DATABASE_API_KEY
    ): ResponseBody
}