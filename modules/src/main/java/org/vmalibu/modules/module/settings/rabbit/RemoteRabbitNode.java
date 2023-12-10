package org.vmalibu.modules.module.settings.rabbit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.json.JSONObject;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.JsonUtils;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RemoteRabbitNode {

    private static final String JSON_ID = "id";

    private final String id;

    public static RemoteRabbitNode load(@NonNull JSONObject jRemoteNode) throws PlatformException {
        return new RemoteRabbitNode(
                JsonUtils.getStringValueNonBlank(jRemoteNode, JSON_ID)
        );
    }
}
