package rebirth.nixaclabs.sbgithubinfo

import com.appmattus.kotlinfixture.kotlinFixture
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import rebirth.nixaclabs.sbgithubinfo.utils.formatDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class SBUtilsTest {

    private val fixture = kotlinFixture()


    /**
     * Test to verify that a valid ISO date string is formatted correctly
     */
    @Test
    fun `formatDate should return formatted date for valid ISO string`() {
        val isoDate = "2024-06-15T10:30:00Z"

        val result = isoDate.formatDate()

        assertThat(result).isNotNull()
        assertThat(result).contains("Jun")
        assertThat(result).contains("15")
        assertThat(result).contains("2024")
        assertThat(result).contains("at")
    }


    /**
     * Test to verify that a valid ISO date with timezone offset is formatted correctly
     */
    @Test
    fun `formatDate should handle ISO date with timezone offset`() {
        val isoDate = "2024-12-25T18:45:30+05:30"

        val result = isoDate.formatDate()

        assertThat(result).isNotNull()
        assertThat(result).contains("Dec")
        assertThat(result).contains("25")
        assertThat(result).contains("2024")
    }


    /**
     * Test to verify that an invalid date string returns null
     */
    @Test
    fun `formatDate should return null for invalid date string`() {
        val invalidDate = "not-a-date"

        val result = invalidDate.formatDate()

        assertThat(result).isNull()
    }


    /**
     * Test to verify that an empty string returns null
     */
    @Test
    fun `formatDate should return null for empty string`() {
        val emptyDate = ""

        val result = emptyDate.formatDate()

        assertThat(result).isNull()
    }


    /**
     * Test to verify that a partial date string returns null
     */
    @Test
    fun `formatDate should return null for partial date string`() {
        val partialDate = "2024-06-15"

        val result = partialDate.formatDate()

        assertThat(result).isNull()
    }


    /**
     * Test using Kotlin Fixture to generate random ZonedDateTime and verify formatting
     */
    @Test
    fun `formatDate should format randomly generated ZonedDateTime`() {
        // Generate a random ZonedDateTime using fixture
        val randomDateTime: ZonedDateTime = fixture()

        // Convert to ISO string format
        val isoString = randomDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)

        val result = isoString.formatDate()

        assertThat(result).isNotNull()
        assertThat(result).contains("at")
        // Verify it contains year from the random date
        assertThat(result).contains(randomDateTime.year.toString())
    }


    /**
     * Test multiple random dates using Kotlin Fixture
     * Just verifies the output is not null and contains expected format markers
     */
    @Test
    fun `formatDate should handle multiple random dates`() {
        repeat(5) {
            val randomDateTime: ZonedDateTime = fixture()
            val isoString = randomDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)

            val result = isoString.formatDate()

            assertThat(result).isNotNull()
            assertThat(result).contains("at")
            assertThat(result).contains(randomDateTime.year.toString())
        }
    }
}
