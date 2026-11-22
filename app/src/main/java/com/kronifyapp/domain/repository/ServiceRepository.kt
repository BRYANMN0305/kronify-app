package com.kronifyapp.domain.repository

import com.kronifyapp.domain.model.BusinessService
import com.kronifyapp.domain.model.ServiceInput

interface ServiceRepository {
    suspend fun listServices(token: String): List<BusinessService>
    suspend fun createService(token: String, input: ServiceInput): BusinessService
    suspend fun updateService(token: String, serviceId: Long, input: ServiceInput): BusinessService
    suspend fun deleteService(token: String, serviceId: Long)
}
