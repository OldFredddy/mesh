package com.chsbk.mesh.service.event;

import com.chsbk.mesh.configuration.CacheNames;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserCacheInvalidationListener {

    private final CacheManager cacheManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void invalidateAfterCommit(UsersChangedEvent event) {
        Cache userCache = cacheManager.getCache(CacheNames.USER_BY_ID);
        if (userCache == null) {
            return;
        }
        for (Long userId : event.userIds()) {
            userCache.evict(userId);
        }
    }
}
