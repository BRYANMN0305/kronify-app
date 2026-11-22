package com.kronifyapp.domain.repository

import com.kronifyapp.domain.model.BusinessRating

interface ReviewRepository {
    suspend fun getBusinessRating(slug: String): BusinessRating
}
