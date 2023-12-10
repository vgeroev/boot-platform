package org.vmalibu.modules.module.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(value = {"stackTrace", "cause", "localizedMessage", "suppressed"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
public class PlatformException extends Exception {

    private final String code;
    private final String message;
    private final Map<String, String> parameters;

    public PlatformException(String code, String message, Map<String, Object> errorParams, Throwable cause) {
        super(cause);
        this.code = code;
        this.message = message;
        this.parameters = new HashMap<>();
        if (errorParams != null) {
            errorParams.forEach((k, v) -> parameters.put(k, String.valueOf(v)));
        }
    }

    public PlatformException(String code, Map<String, Object> errorParams) {
        this(code, null, errorParams, null);
    }

    public PlatformException(String code, String message) {
        this(code, message, new HashMap<>(), null);
    }

    public PlatformException(String code) {
        this(code, (String) null);
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("code", code);
        if (StringUtils.hasText(message)) {
            json.put("message", message);
        }

        if (!parameters.isEmpty()) {
            json.put("parameters", new JSONObject(parameters));
        }
        return json.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != PlatformException.class) {
            return false;
        }

        PlatformException o2 = (PlatformException) other;

        if (parameters.size() != o2.parameters.size()) {
            return false;
        }

        for (Map.Entry<String, String> entry : o2.parameters.entrySet()) {
            if (!Objects.equals(entry.getValue(), parameters.get(entry.getKey()))) {
                return false;
            }
        }

        return Objects.equals(code, o2.code) && Objects.equals(message, o2.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message);
    }
}
