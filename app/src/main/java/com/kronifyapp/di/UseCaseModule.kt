package com.kronifyapp.di

import com.kronifyapp.domain.repository.AppointmentRepository
import com.kronifyapp.domain.repository.AuthRepository
import com.kronifyapp.domain.repository.BusinessRepository
import com.kronifyapp.domain.repository.BusinessScheduleRepository
import com.kronifyapp.domain.repository.EmployeeRepository
import com.kronifyapp.domain.repository.InvitationRepository
import com.kronifyapp.domain.repository.PlanRepository
import com.kronifyapp.domain.repository.ReviewRepository
import com.kronifyapp.domain.repository.ServiceRepository
import com.kronifyapp.domain.repository.UserRepository
import com.kronifyapp.domain.usecase.AddEmployeeServicesUseCase
import com.kronifyapp.domain.usecase.AssignPlanUseCase
import com.kronifyapp.domain.usecase.CancelInvitationUseCase
import com.kronifyapp.domain.usecase.ChangePasswordUseCase
import com.kronifyapp.domain.usecase.CreateBusinessUseCase
import com.kronifyapp.domain.usecase.CreateInvitationUseCase
import com.kronifyapp.domain.usecase.CreateServiceUseCase
import com.kronifyapp.domain.usecase.DeleteServiceUseCase
import com.kronifyapp.domain.usecase.DeleteWeeklyScheduleUseCase
import com.kronifyapp.domain.usecase.GetAuthMethodsUseCase
import com.kronifyapp.domain.usecase.GetAvailablePlansUseCase
import com.kronifyapp.domain.usecase.GetBusinessRatingUseCase
import com.kronifyapp.domain.usecase.GetBusinessReviewsUseCase
import com.kronifyapp.domain.usecase.GetBusinessAgendaUseCase
import com.kronifyapp.domain.usecase.GetBusinessUseCase
import com.kronifyapp.domain.usecase.GetCurrentPlanUseCase
import com.kronifyapp.domain.usecase.GetMyEmployeeUseCase
import com.kronifyapp.domain.usecase.GetOpeningHoursUseCase
import com.kronifyapp.domain.usecase.GetUserProfileUseCase
import com.kronifyapp.domain.usecase.ListEmployeesUseCase
import com.kronifyapp.domain.usecase.ListEmployeeServicesUseCase
import com.kronifyapp.domain.usecase.ListInvitationsUseCase
import com.kronifyapp.domain.usecase.ListServicesUseCase
import com.kronifyapp.domain.usecase.ListWeeklySchedulesUseCase
import com.kronifyapp.domain.usecase.LoginUseCase
import com.kronifyapp.domain.usecase.RegisterUseCase
import com.kronifyapp.domain.usecase.RemoveEmployeeServiceUseCase
import com.kronifyapp.domain.usecase.ResendInvitationUseCase
import com.kronifyapp.domain.usecase.SelectOAuthProfileUseCase
import com.kronifyapp.domain.usecase.ToggleOwnerEmployeeUseCase
import com.kronifyapp.domain.usecase.UpdateBusinessUseCase
import com.kronifyapp.domain.usecase.UpdateOpeningHoursUseCase
import com.kronifyapp.domain.usecase.UpdateSchedulePermissionUseCase
import com.kronifyapp.domain.usecase.UpdateServiceUseCase
import com.kronifyapp.domain.usecase.UpdateUserProfileUseCase
import com.kronifyapp.domain.usecase.UpsertWeeklyScheduleUseCase
import com.kronifyapp.domain.usecase.UploadBusinessLogoUseCase
import com.kronifyapp.domain.usecase.UploadLogoUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides fun provideLoginUseCase(repository: AuthRepository) = LoginUseCase(repository)
    @Provides fun provideRegisterUseCase(repository: AuthRepository) = RegisterUseCase(repository)
    @Provides fun provideSelectOAuthProfileUseCase(repository: AuthRepository) = SelectOAuthProfileUseCase(repository)
    @Provides fun provideCreateBusinessUseCase(repository: AuthRepository) = CreateBusinessUseCase(repository)
    @Provides fun provideUploadBusinessLogoUseCase(repository: AuthRepository) = UploadBusinessLogoUseCase(repository)
    @Provides fun provideGetBusinessUseCase(repository: BusinessRepository) = GetBusinessUseCase(repository)
    @Provides fun provideUpdateBusinessUseCase(repository: BusinessRepository) = UpdateBusinessUseCase(repository)
    @Provides fun provideUploadLogoUseCase(repository: BusinessRepository) = UploadLogoUseCase(repository)
    @Provides fun provideGetOpeningHoursUseCase(repository: BusinessScheduleRepository) = GetOpeningHoursUseCase(repository)
    @Provides fun provideUpdateOpeningHoursUseCase(repository: BusinessScheduleRepository) = UpdateOpeningHoursUseCase(repository)
    @Provides fun provideGetCurrentPlanUseCase(repository: PlanRepository) = GetCurrentPlanUseCase(repository)
    @Provides fun provideGetAvailablePlansUseCase(repository: PlanRepository) = GetAvailablePlansUseCase(repository)
    @Provides fun provideAssignPlanUseCase(repository: PlanRepository) = AssignPlanUseCase(repository)
    @Provides fun provideListEmployeesUseCase(repository: EmployeeRepository) = ListEmployeesUseCase(repository)
    @Provides fun provideGetMyEmployeeUseCase(repository: EmployeeRepository) = GetMyEmployeeUseCase(repository)
    @Provides fun provideUpdateSchedulePermissionUseCase(repository: EmployeeRepository) = UpdateSchedulePermissionUseCase(repository)
    @Provides fun provideListWeeklySchedulesUseCase(repository: EmployeeRepository) = ListWeeklySchedulesUseCase(repository)
    @Provides fun provideUpsertWeeklyScheduleUseCase(repository: EmployeeRepository) = UpsertWeeklyScheduleUseCase(repository)
    @Provides fun provideDeleteWeeklyScheduleUseCase(repository: EmployeeRepository) = DeleteWeeklyScheduleUseCase(repository)
    @Provides fun provideListInvitationsUseCase(repository: InvitationRepository) = ListInvitationsUseCase(repository)
    @Provides fun provideCreateInvitationUseCase(repository: InvitationRepository) = CreateInvitationUseCase(repository)
    @Provides fun provideResendInvitationUseCase(repository: InvitationRepository) = ResendInvitationUseCase(repository)
    @Provides fun provideCancelInvitationUseCase(repository: InvitationRepository) = CancelInvitationUseCase(repository)
    @Provides fun provideGetUserProfileUseCase(repository: UserRepository) = GetUserProfileUseCase(repository)
    @Provides fun provideUpdateUserProfileUseCase(repository: UserRepository) = UpdateUserProfileUseCase(repository)
    @Provides fun provideChangePasswordUseCase(repository: UserRepository) = ChangePasswordUseCase(repository)
    @Provides fun provideGetAuthMethodsUseCase(repository: UserRepository) = GetAuthMethodsUseCase(repository)
    @Provides fun provideGetBusinessRatingUseCase(repository: ReviewRepository) = GetBusinessRatingUseCase(repository)
    @Provides fun provideGetBusinessReviewsUseCase(repository: ReviewRepository) = GetBusinessReviewsUseCase(repository)
    @Provides fun provideGetBusinessAgendaUseCase(repository: AppointmentRepository) = GetBusinessAgendaUseCase(repository)
    @Provides fun provideListServicesUseCase(repository: ServiceRepository) = ListServicesUseCase(repository)
    @Provides fun provideCreateServiceUseCase(repository: ServiceRepository) = CreateServiceUseCase(repository)
    @Provides fun provideUpdateServiceUseCase(repository: ServiceRepository) = UpdateServiceUseCase(repository)
    @Provides fun provideDeleteServiceUseCase(repository: ServiceRepository) = DeleteServiceUseCase(repository)
    @Provides fun provideListEmployeeServicesUseCase(repository: EmployeeRepository) = ListEmployeeServicesUseCase(repository)
    @Provides fun provideAddEmployeeServicesUseCase(repository: EmployeeRepository) = AddEmployeeServicesUseCase(repository)
    @Provides fun provideRemoveEmployeeServiceUseCase(repository: EmployeeRepository) = RemoveEmployeeServiceUseCase(repository)
    @Provides fun provideToggleOwnerEmployeeUseCase(repository: EmployeeRepository) = ToggleOwnerEmployeeUseCase(repository)
}
