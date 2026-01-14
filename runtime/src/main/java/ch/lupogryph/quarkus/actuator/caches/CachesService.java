package ch.lupogryph.quarkus.actuator.caches;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheManager;
import io.quarkus.vertx.http.runtime.devmode.Json;

@ApplicationScoped
public class CachesService {

    private static final Logger log = LoggerFactory.getLogger(CachesService.class);

    @Inject
    CacheManager cacheManager;

    public Json.JsonObjectBuilder allCaches() {
        var object = Json.object();
        var cacheManagers = Json.object();
        var caches = Json.object();
        caches.put("caches", caches());
        cacheManagers.put(cacheManager.getClass().getSimpleName(), caches);
        object.put("cacheManagers", cacheManagers);
        return object;
    }

    public Json.JsonObjectBuilder caches() {
        var object = Json.object();
        cacheManager.getCacheNames().forEach(name -> {
            cacheManager.getCache(name).ifPresent(cache -> object.put(name, target(cache)));
        });
        return object;
    }

    public Json.JsonObjectBuilder target(Cache cache) {
        var object = Json.object();
        object.put("target", cache.getClass().getCanonicalName());
        return object;
    }

    public Json.JsonObjectBuilder cache(String cacheName) {
        var object = Json.object();
        cacheManager.getCache(cacheName).ifPresent(cache -> {
            object.put("target", cache.getClass().getCanonicalName());
            object.put("name", cacheName);
            object.put("cacheManager", cacheManager.getClass().getSimpleName());
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
