package com.kronifyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kronifyapp.data.local.NotificationStore
import com.kronifyapp.data.local.SessionStore
import com.kronifyapp.domain.model.BusinessProfile
import com.kronifyapp.domain.model.BusinessRating
import com.kronifyapp.domain.model.BusinessService
import com.kronifyapp.domain.model.OpeningHour
import com.kronifyapp.domain.model.PlanUsage
import com.kronifyapp.domain.model.UserProfile
import com.kronifyapp.domain.usecase.GetBusinessRatingUseCase
import com.kronifyapp.domain.usecase.GetBusinessUseCase
import com.kronifyapp.domain.usecase.GetCurrentPlanUseCase
import com.kronifyapp.domain.usecase.GetOpeningHoursUseCase
import com.kronifyapp.domain.usecase.GetUserProfileUseCase
import com.kronifyapp.domain.usecase.ListServicesUseCase
import com.kronifyapp.ui.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionStore: SessionStore,
    private val notificationStore: NotificationStore,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getBusinessUseCase: GetBusinessUseCase,
    private val getCurrentPlanUseCase: GetCurrentPlanUseCase,
    private val getBusinessRatingUseCase: GetBusinessRatingUseCase,
    private val listServicesUseCase: ListServicesUseCase,
    private val getOpeningHoursUseCase: GetOpeningHoursUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun load() {
        val token = sessionStore.token
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(isLoading = false, email = sessionStore.email.orEmpty()) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val slug = sessionStore.slug.orEmpty()

            val loaded = coroutineScope {
                val profileDeferred = async { runCatching { getUserProfileUseCase(token) }.getOrNull() }
                val businessDeferred = async { runCatching { getBusinessUseCase(token) }.getOrNull() }
                val planDeferred = async { runCatching { getCurrentPlanUseCase(token) }.getOrNull() }
                val ratingDeferred = async {
                    if (slug.isBlank()) null
                    else runCatching { getBusinessRatingUseCase(slug) }.getOrNull()
                }
                val servicesDeferred = async { runCatching { listServicesUseCase(token) }.getOrNull() }
                val hoursDeferred = async { runCatching { getOpeningHoursUseCase(token) }.getOrNull() }
                LoadedProfile(
                    profile = profileDeferred.await(),
                    business = businessDeferred.await(),
                    plan = planDeferred.await(),
                    rating = ratingDeferred.await(),
                    services = servicesDeferred.await(),
                    hours = hoursDeferred.await(),
                )
            }

            val serviceCount = loaded.services?.size ?: loaded.plan?.serviceCount ?: 0
            val activeServiceCount = loaded.services?.count { it.active } ?: serviceCount

            _uiState.update {
                it.copy(
                    isLoading = false,
                    name = loaded.profile?.name ?: sessionStore.name.orEmpty(),
                    lastName = loaded.profile?.lastName.orEmpty(),
                    email = loaded.profile?.email ?: sessionStore.email.orEmpty(),
                    businessName = loaded.business?.name.orEmpty(),
                    businessCategory = loaded.business?.category.orEmpty(),
                    planName = loaded.plan?.plan?.displayName
                        ?.takeIf { name -> name.isNotBlank() }
                        ?: loaded.plan?.plan?.name.orEmpty(),
                    rating = loaded.rating?.average,
                    reviewCount = loaded.rating?.count ?: 0,
                    monthlyAppointments = loaded.plan?.currentMonthAppointmentCount ?: 0,
                    serviceCount = serviceCount,
                    activeServiceCount = activeServiceCount,
                    employeeCount = loaded.plan?.employeeCount ?: 0,
                    employeeLimit = loaded.plan?.plan?.employeeLimit,
                    scheduleSummary = formatScheduleSummary(loaded.hours.orEmpty()),
                    notificationsEnabled = notificationStore.enabled,
                    slug = slug,
                )
            }
        }
    }

    private fun formatScheduleSummary(hours: List<OpeningHour>): String {
        if (hours.isEmpty()) return ""
        val labels = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
        val sorted = hours.sortedBy { it.dayOfWeek }
        val parts = mutableListOf<String>()
        var index = 0
        while (index < sorted.size) {
            val start = sorted[index]
            var endIndex = index
            while (
                endIndex + 1 < sorted.size &&
                sorted[endIndex + 1].dayOfWeek == sorted[endIndex].dayOfWeek + 1 &&
                sorted[endIndex + 1].startTime == start.startTime &&
                sorted[endIndex + 1].endTime == start.endTime
            ) {
                endIndex++
            }
            val dayLabel = if (index == endIndex) {
                labels[start.dayOfWeek - 1]
            } else {
                "${labels[start.dayOfWeek - 1]}–${labels[sorted[endIndex].dayOfWeek - 1]}"
            }
            parts.add("$dayLabel ${start.startTime}–${start.endTime}")
            index = endIndex + 1
        }
        return parts.joinToString(" · ")
    }

    private data class LoadedProfile(
        val profile: UserProfile?,
        val business: BusinessProfile?,
        val plan: PlanUsage?,
        val rating: BusinessRating?,
        val services: List<BusinessService>?,
        val hours: List<OpeningHour>?,
    )
}
