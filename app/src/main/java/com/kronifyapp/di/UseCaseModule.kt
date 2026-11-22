package com.kronifyapp.di

import com.kronifyapp.domain.repository.AuthRepository
import com.kronifyapp.domain.usecase.CreateBusinessUseCase
import com.kronifyapp.domain.usecase.LoginUseCase
import com.kronifyapp.domain.usecase.RegisterUseCase
import com.kronifyapp.domain.usecase.UploadBusinessLogoUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides fun provideLoginUseCase(repository: AuthRepository) = LoginUseCase(repository)
    @Provides fun provideRegisterUseCase(repository: AuthRepository) = RegisterUseCase(repository)
    @Provides fun provideCreateBusinessUseCase(repository: AuthRepository) = CreateBusinessUseCase(repository)
    @Provides fun provideUploadBusinessLogoUseCase(repository: AuthRepository) = UploadBusinessLogoUseCase(repository)
}
