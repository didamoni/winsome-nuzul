package com.winsome.nuzul.domain.usecase.hotels

import com.winsome.nuzul.domain.model.Hotel
import com.winsome.nuzul.domain.repository.HotelsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHotelDetailsUseCase @Inject constructor(
    private val hotelsRepository: HotelsRepository
) {
    operator fun invoke(hotelId: String): Flow<Hotel?> {
        return hotelsRepository.getHotelById(hotelId)
    }
}