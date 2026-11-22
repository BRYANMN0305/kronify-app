package com.kronifyapp.di

import com.kronifyapp.data.repository.AppointmentRepositoryImpl
import com.kronifyapp.data.repository.AuthRepositoryImpl
import com.kronifyapp.data.repository.BusinessRepositoryImpl
import com.kronifyapp.data.repository.BusinessScheduleRepositoryImpl
import com.kronifyapp.data.repository.EmployeeRepositoryImpl
import com.kronifyapp.data.repository.InvitationRepositoryImpl
import com.kronifyapp.data.repository.PlanRepositoryImpl
import com.kronifyapp.data.repository.ReviewRepositoryImpl
import com.kronifyapp.data.repository.ServiceRepositoryImpl
import com.kronifyapp.data.repository.UserRepositoryImpl
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
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(implementation: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindBusinessRepository(implementation: BusinessRepositoryImpl): BusinessRepository

    @Binds
    @Singleton
    abstract fun bindBusinessScheduleRepository(implementation: BusinessScheduleRepositoryImpl): BusinessScheduleRepository

    @Binds
    @Singleton
    abstract fun bindPlanRepository(implementation: PlanRepositoryImpl): PlanRepository

    @Binds
    @Singleton
    abstract fun bindEmployeeRepository(implementation: EmployeeRepositoryImpl): EmployeeRepository

    @Binds
    @Singleton
    abstract fun bindInvitationRepository(implementation: InvitationRepositoryImpl): InvitationRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(implementation: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindReviewRepository(implementation: ReviewRepositoryImpl): ReviewRepository

    @Binds
    @Singleton
    abstract fun bindServiceRepository(implementation: ServiceRepositoryImpl): ServiceRepository

    @Binds
    @Singleton
    abstract fun bindAppointmentRepository(implementation: AppointmentRepositoryImpl): AppointmentRepository
}
