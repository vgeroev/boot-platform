package org.vmalibu.modules.module.exception;

import org.vmalibu.modules.database.domainobject.DomainObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GeneralExceptionBuilder {

    public static final String INVALID_VALUE_CODE = "invalid_value";
    public static final String INTERNAL_SERVER_ERROR_CODE = "internal_server_error";
    public static final String NOT_FOUND_DOMAIN_OBJECT_CODE = "not_found_domain_object";
    public static final String NOT_UNIQUE_DOMAIN_OBJECT_CODE = "not_unique_domain_object";
    public static final String EMPTY_VALUE_CODE = "empty_value";
    public static final String INVALID_ARGUMENT_CODE = "invalid_argument";
    public static final String IO_ERROR_CODE = "io_error";
    public static final String JSON_PARSING_ERROR_CODE = "json_parsing_error";

    private GeneralExceptionBuilder() {
    }

    public static PlatformException buildInvalidValueException(String fieldName) {
        return new PlatformException(INVALID_VALUE_CODE, new HashMap<>() {{
            put("fieldName", fieldName);
        }});
    }

    public static PlatformException buildInternalServerErrorException(Throwable e) {
        return new PlatformException(INTERNAL_SERVER_ERROR_CODE, e.getMessage(), null, e);
    }

    public static PlatformException buildInternalServerErrorException(String message) {
        return new PlatformException(INTERNAL_SERVER_ERROR_CODE, message);
    }

    public static PlatformException buildNotFoundDomainObjectException(Class<? extends DomainObject> clazz, long id) {
        return new PlatformException(NOT_FOUND_DOMAIN_OBJECT_CODE, new HashMap<>() {{
            put("id", id);
            put("class", clazz.getSimpleName());
        }});
    }

    public static PlatformException buildNotFoundDomainObjectException(Class<? extends DomainObject> clazz,
                                                                       String fieldName,
                                                                       Serializable value) {
        return new PlatformException(NOT_FOUND_DOMAIN_OBJECT_CODE, new HashMap<>() {{
            put("fieldName", fieldName);
            put("value", value);
            put("class", clazz.getSimpleName());
        }});
    }

    public static PlatformException buildNotUniqueDomainObjectException(Class<? extends DomainObject> clazz,
                                                                        String fieldName,
                                                                        Serializable value) {
        return new PlatformException(NOT_UNIQUE_DOMAIN_OBJECT_CODE, new HashMap<>() {{
            put("class", clazz.getSimpleName());
            put("fieldName", fieldName);
            put("value", value);
        }});
    }

    public static PlatformException buildEmptyValueException(Class<? extends DomainObject> clazz,
                                                             String fieldName) {
        return new PlatformException(EMPTY_VALUE_CODE, new HashMap<>() {{
            put("class", clazz.getSimpleName());
            put("fieldName", fieldName);
        }});
    }

    public static PlatformException buildEmptyValueException(String fieldName) {
        return new PlatformException(EMPTY_VALUE_CODE, new HashMap<>() {{
            put("fieldName", fieldName);
        }});
    }

    public static PlatformException buildInvalidArgumentException(String message) {
        return new PlatformException(INVALID_ARGUMENT_CODE, new HashMap<>() {{
            put("message", message);
        }});
    }

    public static PlatformException buildIOErrorException(IOException e) {
        return new PlatformException(IO_ERROR_CODE, null, null, e);
    }

    public static PlatformException buildJsonParsingErrorException(String message) {
        Map<String, Object> params = new HashMap<>();
        params.put("message", message);
        return new PlatformException(JSON_PARSING_ERROR_CODE, params);
    }

    public static PlatformException buildJsonParsingErrorException(Throwable e) {
        return new PlatformException(JSON_PARSING_ERROR_CODE, null, null, e);
    }

}
