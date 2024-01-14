package org.vmalibu.modules.module.exception;

import org.json.JSONObject;
import org.vmalibu.modules.database.domainobject.DomainObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GeneralExceptionFactory {

    public static final String INVALID_VALUE_CODE = "invalid_value";
    public static final String INTERNAL_SERVER_ERROR_CODE = "internal_server_error";
    public static final String NOT_FOUND_DOMAIN_OBJECT_CODE = "not_found_domain_object";
    public static final String NOT_UNIQUE_DOMAIN_OBJECT_CODE = "not_unique_domain_object";
    public static final String EMPTY_VALUE_CODE = "empty_value";
    public static final String INVALID_ARGUMENT_CODE = "invalid_argument";
    public static final String IO_ERROR_CODE = "io_error";
    public static final String JSON_PARSING_ERROR_CODE = "json_parsing_error";

    private GeneralExceptionFactory() {
    }

    public static PlatformException buildInvalidValueException(String fieldName) {
        Map<String, Object> params = new HashMap<>();
        params.put("fieldName", fieldName);
        return new PlatformException(INVALID_VALUE_CODE, params);
    }

    public static PlatformException buildInternalServerErrorException(Throwable e) {
        return new PlatformException(INTERNAL_SERVER_ERROR_CODE, e.getMessage(), null, e);
    }

    public static PlatformException buildInternalServerErrorException(String message) {
        return new PlatformException(INTERNAL_SERVER_ERROR_CODE, message);
    }

    public static PlatformException buildNotFoundDomainObjectException(Class<? extends DomainObject> clazz, long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("class", clazz.getSimpleName());
        return new PlatformException(NOT_FOUND_DOMAIN_OBJECT_CODE, params);
    }

    public static PlatformException buildNotFoundDomainObjectException(Class<? extends DomainObject> clazz,
                                                                       String fieldName,
                                                                       Serializable value) {
        Map<String, Object> params = new HashMap<>();
        params.put("fieldName", fieldName);
        params.put("value", value);
        params.put("class", clazz.getSimpleName());
        return new PlatformException(NOT_FOUND_DOMAIN_OBJECT_CODE, params);
    }

    public static PlatformException buildNotUniqueDomainObjectException(Class<? extends DomainObject> clazz,
                                                                        Map<String, Serializable> fields) {
        Map<String, Object> params = new HashMap<>();
        params.put("class", clazz.getSimpleName());
        params.put("fields", new JSONObject(fields).toString());
        return new PlatformException(NOT_UNIQUE_DOMAIN_OBJECT_CODE, params);
    }

    public static PlatformException buildNotUniqueDomainObjectException(Class<? extends DomainObject> clazz,
                                                                        String fieldName,
                                                                        Serializable value) {
        return buildNotUniqueDomainObjectException(clazz, Map.of(fieldName, value));
    }

    public static PlatformException buildEmptyValueException(Class<? extends DomainObject> clazz,
                                                             String fieldName) {
        Map<String, Object> params = new HashMap<>();
        params.put("class", clazz.getSimpleName());
        params.put("fieldName", fieldName);
        return new PlatformException(EMPTY_VALUE_CODE, params);
    }

    public static PlatformException buildEmptyValueException(String fieldName) {
        Map<String, Object> params = new HashMap<>();
        params.put("fieldName", fieldName);
        return new PlatformException(EMPTY_VALUE_CODE, params);
    }

    public static PlatformException buildInvalidArgumentException(String message) {
        Map<String, Object> params = new HashMap<>();
        params.put("message", message);
        return new PlatformException(INVALID_ARGUMENT_CODE, params);
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
