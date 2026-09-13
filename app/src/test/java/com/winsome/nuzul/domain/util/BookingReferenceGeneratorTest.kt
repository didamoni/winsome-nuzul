package com.winsome.nuzul.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BookingReferenceGeneratorTest {

    @Test
    fun generate_defaultLength_returnsValidFormatAndLength() {
        val reference = BookingReferenceGenerator.generate()

        // Check format prefix NZL-
        assertTrue("Reference should start with NZL-", reference.startsWith("NZL-"))

        // Default length is 6, plus prefix "NZL-" (4 chars), total length should be 10
        assertEquals(10, reference.length)

        // Verify the code part contains only valid characters
        val codePart = reference.substringAfter("NZL-")
        assertEquals(6, codePart.length)
        val validChars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        for (char in codePart) {
            assertTrue("Character '$char' should be in the allowed set", validChars.contains(char))
        }
    }

    @Test
    fun generate_customLength_returnsCorrectLength() {
        val customLength = 8
        val reference = BookingReferenceGenerator.generate(length = customLength)

        assertTrue("Reference should start with NZL-", reference.startsWith("NZL-"))
        // Prefix length 4 + custom length 8 = 12
        assertEquals(4 + customLength, reference.length)

        val codePart = reference.substringAfter("NZL-")
        assertEquals(customLength, codePart.length)
    }

    @Test
    fun generate_multipleCalls_generatesUniqueReferences() {
        val references = (1..50).map { BookingReferenceGenerator.generate() }.toSet()
        // With 50 generations, they should all be unique due to SecureRandom space
        assertEquals(50, references.size)
    }
}
