package com.kronifyapp.data.remote.response

data class ReviewResponse(
    val reviewId: Long = 0L,
    val rating: Int = 0,
    val comment: String? = null,
    val visible: Boolean = true,
    val createdAt: String? = null,
    val customerName: String? = null,
)
