package com.kronifyapp.ui.state

import com.kronifyapp.domain.model.BusinessReview

data class ReviewsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val average: Double = 0.0,
    val reviews: List<BusinessReview> = emptyList(),
)
