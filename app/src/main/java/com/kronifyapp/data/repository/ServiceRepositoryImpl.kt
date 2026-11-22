package com.kronifyapp.data.repository

import com.kronifyapp.data.mapper.request.toRequest
import com.kronifyapp.data.mapper.response.toDomain
import com.kronifyapp.data.remote.ServiceApi
import com.kronifyapp.domain.model.BusinessService
import com.kronifyapp.domain.model.ServiceInput
import com.kronifyapp.domain.repository.ServiceRepository
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(
    private val serviceApi: ServiceApi,
) : ServiceRepository {
    override suspend fun listServices(token: String): List<BusinessService> =
        serviceApi.listServices("Bearer $token").map { it.toDomain() }

    override suspend fun createService(token: String, input: ServiceInput): BusinessService =
        serviceApi.createService("Bearer $token", input.toRequest()).toDomain()

    override suspend fun updateService(token: String, serviceId: Long, input: ServiceInput): BusinessService =
        serviceApi.updateService("Bearer $token", serviceId, input.toRequest()).toDomain()

    override suspend fun deleteService(token: String, serviceId: Long) {
        serviceApi.deleteService("Bearer $token", serviceId)
    }
}
