package ch.lupogryph.quarkus.actuator.caches;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheManager;

@ApplicationScoped
public class CachesService {

    private static final Logger log = LoggerFactory.getLogger(CachesService.class);

    @Inject
    CacheManager cacheManager;

    public JsonObjectBuilder allCaches() {
        var object = Json.createObjectBuilder();
        var cacheManagers = Json.createObjectBuilder();
        var caches = Json.createObjectBuilder();
        caches.add("caches", caches());
        cacheManagers.add(cacheManager.getClass().getSimpleName(), caches);
        object.add("cacheManagers", cacheManagers);
        return object;
    }

    public JsonObjectBuilder caches() {
        var object = Json.createObjectBuilder();
        cacheManager.getCacheNames().forEach(name -> {
            cacheManager.getCache(name).ifPresent(cache -> object.add(name, target(cache)));
        });
        return object;
    }

    public JsonObjectBuilder target(Cache cache) {
        var object = Json.createObjectBuilder();
        object.add("target", cache.getClass().getCanonicalName());
        return object;
    }

    public JsonObjectBuilder cache(String cacheName) {
        var object = Json.createObjectBuilder();
        cacheManager.getCache(cacheName).ifPresent(cache -> {
            object.add("target", cache.getClass().getCanonicalName());
            object.add("name", cacheName);
            object.add("cacheManager", cacheManager.getClass().getSimpleName());
        });
        return object;
    }

    public Void clearAllCaches() {
        cacheManager.getCacheNames().forEach(this::clearCache);
        return null;
    }

    public Void clearCache(String name) {
        cacheManager.getCache(name).ifPresent(cache -> {
            cache.invalidateAll().subscribe().with(
                    ok -> log.info("Cache {} cleared", name),
                    err -> log.error("Cache {} clear failed : {}", name, err.getMessage()));
        });
        cacheManager.getCache(name).ifPresent(Cache::invalidateAll);
        return null;
    }

}
