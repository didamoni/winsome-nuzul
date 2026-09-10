package com.winsome.nuzul.domain.usecase.hotels

import com.winsome.nuzul.domain.model.Hotel
import com.winsome.nuzul.domain.model.HotelFilter
import com.winsome.nuzul.domain.repository.HotelsRepository
import javax.inject.Inject

class GetHotelsUseCase @Inject constructor(
    private val hotelsRepository: HotelsRepository
) {
    suspend operator fun invoke(
        filter: HotelFilter,
        page: Int,
        pageSize: Int = 10
    ): Result<List<Hotel>> {
        return hotelsRepository.getHotels(filter, page, pageSize)
    }
}