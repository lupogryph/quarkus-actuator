package ch.lupogryph.quarkus.actuator.caches;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class CachesServiceTest {

    private static final String CACHE_NAME = "test-cache";

    @Inject
    CachesService cachesService;

    @CacheName(CACHE_NAME)
    Cache cache;

    @Test
    void cacheManagersTest() {
        var cacheManagers = cachesService.allCaches().build().toString();
        assertThat(cacheManagers).isNotBlank();
    }

    @Test
    void cacheTest() {
        var res = cachesService.cache(CACHE_NAME).build().toString();
        assertThat(res).isNotBlank().startsWith("""
                {"target":"io.quarkus.cache.runtime.caffeine.CaffeineCacheImpl","name":"%s","cacheManager":"CacheManager_"""
                .formatted(CACHE_NAME));
    }

}
