package com.kronifyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import com.kronifyapp.data.local.NotificationStore
import com.kronifyapp.ui.state.NotificationsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationStore: NotificationStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationsUiState(enabled = notificationStore.enabled))
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    fun setEnabled(enabled: Boolean) {
        notificationStore.enabled = enabled
        _uiState.update { it.copy(enabled = enabled) }
    }
}
