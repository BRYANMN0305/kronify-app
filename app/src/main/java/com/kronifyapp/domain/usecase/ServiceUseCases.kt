package com.kronifyapp.domain.usecase

import com.kronifyapp.domain.model.ServiceInput
import com.kronifyapp.domain.repository.ServiceRepository

class ListServicesUseCase(private val repository: ServiceRepository) {
    suspend operator fun invoke(token: String) = repository.listServices(token)
}

class CreateServiceUseCase(private val repository: ServiceRepository) {
    suspend operator fun invoke(token: String, input: ServiceInput) = repository.createService(token, input)
}

class UpdateServiceUseCase(private val repository: ServiceRepository) {
    suspend operator fun invoke(token: String, serviceId: Long, input: ServiceInput) =
        repository.updateService(token, serviceId, input)
}

class DeleteServiceUseCase(private val repository: ServiceRepository) {
    suspend operator fun invoke(token: String, serviceId: Long) = repository.deleteService(token, serviceId)
}
