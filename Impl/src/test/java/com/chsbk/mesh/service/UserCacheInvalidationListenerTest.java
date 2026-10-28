package com.chsbk.mesh.service;

import com.chsbk.mesh.configuration.CacheNames;
import com.chsbk.mesh.service.event.UserCacheInvalidationListener;
import com.chsbk.mesh.service.event.UsersChangedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCacheInvalidationListenerTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private UserCacheInvalidationListener invalidationListener;

    @Test
    void evictsOnlyChangedUsers() {
        when(cacheManager.getCache(CacheNames.USER_BY_ID)).thenReturn(cache);

        invalidationListener.invalidateAfterCommit(new UsersChangedEvent(Set.of(1L, 2L)));

        verify(cache).evict(1L);
        verify(cache).evict(2L);
    }
}
