package dev.fincke.hopper.platforms

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DataJpaTest
class PlatformRepositoryTest(
    @Autowired val repo: PlatformRepository
) {
    @Test
    fun `can insert and find by type`() {
        val saved = repo.save(Platform(name = "Main eBay", platformType = "EBAY"))
        assertNotNull(saved.id)
        val found = repo.findByPlatformType("EBAY")
        assertTrue(found.any { it.name == "Main eBay" })
    }

    @Test
    fun `name is unique`() {
        repo.save(Platform(name = "TCGplayer Shop", platformType = "TCGPLAYER"))
        val ex = org.junit.jupiter.api.assertThrows<DataIntegrityViolationException> {
            repo.saveAndFlush(Platform(name = "TCGplayer Shop", platformType = "TCGPLAYER"))
        }
        assertEquals(DataIntegrityViolationException::class, ex::class)
    }
}