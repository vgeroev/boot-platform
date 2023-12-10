package org.vmalibu.modules.module.settings.rabbit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ClusterRabbitSettings {

    private static final String JSON_LOCAL = "local";
    private static final String JSON_REMOTES = "remotes";

    private final LocalRabbitNode localRabbitNode;
    private final List<RemoteRabbitNode> remoteRabbitNodes;

    public static ClusterRabbitSettings load(@NonNull JSONObject jSettings) throws PlatformException {
        JSONObject jLocal = JsonUtils.getValue(jSettings, JSON_LOCAL, true, JSONObject.class);
        if (jLocal == null) {
            throw new IllegalStateException("Settings for local node is null");
        }

        LocalRabbitNode localRabbitNode = LocalRabbitNode.load(jLocal);

        //--------------------------------------------------------------------------------------------------------------

        JSONArray jRemotes = JsonUtils.getValue(jSettings, JSON_REMOTES, false, JSONArray.class);
        List<RemoteRabbitNode> remoteRabbitNodes = new ArrayList<>();
        if (jRemotes != null) {
            for (Object item : jRemotes) {
                remoteRabbitNodes.add(RemoteRabbitNode.load((JSONObject) item));
            }
        }

        //--------------------------------------------------------------------------------------------------------------

        return new ClusterRabbitSettings(localRabbitNode, remoteRabbitNodes);
    }

    public static @NonNull JSONObject getDefault() {
        JSONObject jClusterSettings = new JSONObject();
        jClusterSettings.put(JSON_LOCAL, LocalRabbitNode.getDefault());
        jClusterSettings.put(JSON_REMOTES, new JSONArray());
        return jClusterSettings;
    }

}
