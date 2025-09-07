package dev.fincke.hopper.platforms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PlatformRepositoryTest 
{

    @Autowired
    private PlatformRepository repo;

    @Test
    void canInsertAndFindByType() 
    {
        Platform saved = repo.save(new Platform("Main eBay", "EBAY"));
        assertNotNull(saved.getId());
        
        List<Platform> found = repo.findByPlatformType("EBAY");
        assertTrue(found.stream().anyMatch(p -> "Main eBay".equals(p.getName())));
    }

    @Test
    void nameIsUnique() 
    {
        repo.save(new Platform("TCGplayer Shop", "TCGPLAYER"));
        
        assertThrows(DataIntegrityViolationException.class, () -> 
        {
            repo.saveAndFlush(new Platform("TCGplayer Shop", "TCGPLAYER"));
        });
    }
}