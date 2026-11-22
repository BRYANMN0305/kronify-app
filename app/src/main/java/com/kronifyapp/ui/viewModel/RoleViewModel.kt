package com.kronifyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kronifyapp.data.local.SessionStore
import com.kronifyapp.domain.usecase.GetMyEmployeeUseCase
import com.kronifyapp.domain.usecase.ListEmployeesUseCase
import com.kronifyapp.ui.state.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class RoleViewModel @Inject constructor(
    private val sessionStore: SessionStore,
    private val listEmployeesUseCase: ListEmployeesUseCase,
    private val getMyEmployeeUseCase: GetMyEmployeeUseCase,
) : ViewModel() {
    private val _role = MutableStateFlow(initialRole())
    val role: StateFlow<UserRole> = _role.asStateFlow()

    init {
        resolve()
    }

    private fun initialRole(): UserRole = when (sessionStore.profileType) {
        "CLIENT" -> UserRole.CLIENT
        "BUSINESS" -> UserRole.LOADING
        else -> UserRole.CLIENT
    }

    private fun resolve() {
        if (sessionStore.profileType == "CLIENT") {
            _role.value = UserRole.CLIENT
            return
        }
        if (sessionStore.profileType != "BUSINESS") {
            _role.value = UserRole.CLIENT
            return
        }

        val token = sessionStore.token
        if (token.isNullOrBlank()) {
            _role.value = UserRole.CLIENT
            return
        }

        _role.value = UserRole.LOADING
        viewModelScope.launch {
            val isOwner = runCatching { listEmployeesUseCase(token) }.isSuccess
            if (isOwner) {
                _role.value = UserRole.OWNER
                return@launch
            }

            val isEmployee = runCatching { getMyEmployeeUseCase(token) }.isSuccess
            _role.value = if (isEmployee) UserRole.EMPLOYEE else UserRole.OWNER
        }
    }
}
