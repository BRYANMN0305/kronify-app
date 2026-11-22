package com.kronifyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import com.kronifyapp.data.local.SessionStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionStore: SessionStore,
) : ViewModel() {
    fun clearSession() = sessionStore.clear()
}
