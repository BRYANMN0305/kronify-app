package com.kronifyapp.domain.repository

import com.kronifyapp.domain.model.BusinessRating
import com.kronifyapp.domain.model.BusinessReview

interface ReviewRepository {
    suspend fun getBusinessRating(slug: String): BusinessRating
    suspend fun getBusinessReviews(slug: String): List<BusinessReview>
}
