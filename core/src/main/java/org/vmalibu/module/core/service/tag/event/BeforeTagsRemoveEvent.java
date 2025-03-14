package org.vmalibu.module.core.service.tag.event;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;

public record BeforeTagsRemoveEvent(@NonNull Set<@NonNull Long> ids) {
}
