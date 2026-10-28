package com.chsbk.mesh.service.event;

import java.util.Set;

public record UsersChangedEvent(Set<Long> userIds) {
    public UsersChangedEvent {
        userIds = Set.copyOf(userIds);
    }
}
