package com.winsome.nuzul.domain.usecase.hotels

import com.winsome.nuzul.domain.repository.HotelsRepository
import javax.inject.Inject

class GetCitiesUseCase @Inject constructor(
    private val hotelsRepository: HotelsRepository
) {
    suspend operator fun invoke(): Result<List<String>> {
        return hotelsRepository.getCities()
    }
}