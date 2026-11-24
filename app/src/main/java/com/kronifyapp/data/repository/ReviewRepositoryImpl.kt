package com.kronifyapp.data.repository

import com.kronifyapp.data.remote.ReviewApi
import com.kronifyapp.domain.model.BusinessRating
import com.kronifyapp.domain.model.BusinessReview
import com.kronifyapp.domain.repository.ReviewRepository
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val reviewApi: ReviewApi,
) : ReviewRepository {
    override suspend fun getBusinessReviews(slug: String): List<BusinessReview> =
        reviewApi.getPublicReviews(slug)
            .filter { it.visible }
            .map { BusinessReview(it.reviewId, it.rating, it.comment, it.createdAt, it.customerName) }
            .sortedByDescending { it.createdAt.orEmpty() }

    override suspend fun getBusinessRating(slug: String): BusinessRating {
        val visible = reviewApi.getPublicReviews(slug).filter { it.visible }
        if (visible.isEmpty()) return BusinessRating()
        val average = visible.sumOf { it.rating }.toDouble() / visible.size
        return BusinessRating(average = average, count = visible.size)
    }
}
