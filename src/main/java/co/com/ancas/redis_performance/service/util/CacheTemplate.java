package co.com.ancas.redis_performance.service.util;

import reactor.core.publisher.Mono;

public abstract class CacheTemplate<KEY, ENTITY> {

    public Mono<ENTITY> get(KEY key) {
        return getFromCache(key)
                .switchIfEmpty(getFromSource(key)
                        .flatMap(entity -> updateCache(key, entity).thenReturn(entity)));
    }

    public Mono<ENTITY> update(KEY key, ENTITY entityMono) {
        return this.updateSource(key, entityMono)
                .flatMap(e->deleteFromCache(key).thenReturn(e));
    }

    public Mono<Void> delete(KEY key) {
        return deleteFromSource(key)
                .then(deleteFromCache(key));
    }

    abstract protected Mono<ENTITY> getFromSource(KEY key);
    abstract protected Mono<ENTITY> getFromCache(KEY key);
    abstract protected Mono<ENTITY> updateSource(KEY key, ENTITY entity);
    abstract protected Mono<ENTITY> updateCache(KEY key, ENTITY entity);
    abstract protected Mono<Void> deleteFromSource(KEY key);
    abstract protected Mono<Void> deleteFromCache(KEY key);
}
