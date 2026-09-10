package com.winsome.nuzul.domain.util

import java.security.SecureRandom

object BookingReferenceGenerator {
    private const val PREFIX = "NZL"
    private const val CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    private val random = SecureRandom()

    fun generate(length: Int = 6): String {
        val code = (1..length)
            .map { CHARACTERS[random.nextInt(CHARACTERS.length)] }
            .joinToString("")
        return "$PREFIX-$code"
    }
}