package org.vmalibu.module.security.service.accessrole;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

import java.util.Map;
import java.util.Set;

public interface AccessRoleService {

    @Nullable AccessRoleDTO findById(long id);

    @NonNull AccessRoleDTO create(@NonNull String name) throws PlatformException;

    @NonNull AccessRoleDTO update(long id,
                                  @NonNull OptionalField<String> name,
                                  @NonNull OptionalField<Map<String, Set<AccessOp>>> privileges) throws PlatformException;

    void remove(long id) throws PlatformException;

}
