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
public class LocalRabbitNode {

    private static final String JSON_ID = "id";
    private static final String JSON_HOST = "host";
    private static final String JSON_PORT = "port";
    private static final String JSON_USERNAME = "username";
    private static final String JSON_PASSWORD = "password";

    private final String id;
    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public static LocalRabbitNode load(@NonNull JSONObject jLocalNode) throws PlatformException {
        return new LocalRabbitNode(
                JsonUtils.getStringValueNonBlank(jLocalNode, JSON_ID),
                JsonUtils.getStringValueNonBlank(jLocalNode, JSON_HOST),
                JsonUtils.getIntegerValue(jLocalNode, JSON_PORT),
                JsonUtils.getStringValueNonBlank(jLocalNode, JSON_USERNAME),
                JsonUtils.getStringValueNonBlank(jLocalNode, JSON_PASSWORD)
        );
    }

    public static @NonNull JSONObject getDefault() {
        JSONObject jNode = new JSONObject();
        jNode.put(JSON_ID, "local");
        jNode.put(JSON_HOST, "127.0.0.1");
        jNode.put(JSON_PORT, 5672);
        jNode.put(JSON_USERNAME, "guest");
        jNode.put(JSON_PASSWORD, "guest");

        return jNode;
    }


}
