package com.winsome.nuzul.domain.usecase.hotels

import com.winsome.nuzul.domain.repository.HotelsRepository
import javax.inject.Inject

class RefreshHotelsUseCase @Inject constructor(
    private val hotelsRepository: HotelsRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return hotelsRepository.refreshHotels()
    }
}