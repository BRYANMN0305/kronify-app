package com.kronifyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kronifyapp.data.local.SessionStore
import com.kronifyapp.domain.usecase.GetBusinessRatingUseCase
import com.kronifyapp.domain.usecase.GetBusinessReviewsUseCase
import com.kronifyapp.ui.state.ReviewsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    private val sessionStore: SessionStore,
    private val getBusinessRatingUseCase: GetBusinessRatingUseCase,
    private val getBusinessReviewsUseCase: GetBusinessReviewsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReviewsUiState())
    val uiState = _uiState.asStateFlow()

    init { load() }

    fun load() {
        val slug = sessionStore.slug
        if (slug.isNullOrBlank()) {
            _uiState.update { it.copy(isLoading = false, error = "No encontramos el enlace público del negocio.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val reviews = getBusinessReviewsUseCase(slug)
                val rating = getBusinessRatingUseCase(slug)
                reviews to rating
            }.onSuccess { (reviews, rating) ->
                _uiState.update { it.copy(isLoading = false, reviews = reviews, average = rating.average) }
            }.onFailure { error -> _uiState.update { it.copy(isLoading = false, error = error.message ?: "No se pudieron cargar las reseñas.") } }
        }
    }
}
