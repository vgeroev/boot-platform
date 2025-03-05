package org.vmalibu.module.core.service.tag;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;

public interface TagActionsListener {

    void onBeforeRemove(@NonNull Set<@NonNull Long> ids);
}
