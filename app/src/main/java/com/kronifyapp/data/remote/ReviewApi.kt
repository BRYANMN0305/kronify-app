package com.kronifyapp.data.remote

import com.kronifyapp.data.remote.response.ReviewResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ReviewApi {
    @GET("public/businesses/{slug}/reviews")
    suspend fun getPublicReviews(@Path("slug") slug: String): List<ReviewResponse>
}
