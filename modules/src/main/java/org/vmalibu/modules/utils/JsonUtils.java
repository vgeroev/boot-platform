package org.vmalibu.modules.utils;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.vmalibu.modules.module.exception.GeneralExceptionBuilder;
import org.vmalibu.modules.module.exception.PlatformException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonUtils {

    private JsonUtils() { }

    public static String getStringValue(@NonNull JSONObject json,
                                        @NonNull String fieldName) throws PlatformException {
        return getValue(json, fieldName, true, String.class);
    }

    public static String getStringValueNonBlank(@NonNull JSONObject json,
                                                @NonNull String fieldName) throws PlatformException {
        String value = getStringValue(json, fieldName);
        if (value == null || value.isBlank()) {
            throw GeneralExceptionBuilder.buildJsonParsingErrorException("Value is null or blank for json field=" + fieldName);
        }

        return value;
    }

    public static int getIntegerValue(@NonNull JSONObject json,
                                      @NonNull String fieldName) throws PlatformException {
        Integer value = getValue(json, fieldName, true, Integer.class);
        if (value == null) {
            throw GeneralExceptionBuilder.buildJsonParsingErrorException("Value is null for json field=" + fieldName);
        }

        return value;
    }

    public static <T> @Nullable T getValue(@NonNull JSONObject json,
                                           @NonNull String fieldName,
                                           boolean checkFieldExistence,
                                           @NonNull Class<T> clazz) throws PlatformException {
        Object value;
        if (checkFieldExistence) {
            checkFieldExistence(json, fieldName);
            value = json.get(fieldName);
        } else {
            if (!json.has(fieldName)) {
                return null;
            }
            value = json.get(fieldName);
        }

        Class<?> valueClass = value.getClass();
        if (!clazz.isAssignableFrom(valueClass)) {
            throw GeneralExceptionBuilder.buildJsonParsingErrorException("%s cannot be casted to %s".formatted(value, clazz));
        }

        return clazz.cast(value);
    }


    public static void checkFieldExistence(@NonNull JSONObject json, @NonNull String name) throws PlatformException {
        if (!json.has(name)) {
            throw GeneralExceptionBuilder.buildJsonParsingErrorException("There is no field with name=" + name);
        }
    }

    public static @NonNull JSONObject readJson(@NonNull Path path) throws PlatformException {
        try {
            JSONTokener jsonTokener = new JSONTokener(Files.readString(path));
            return new JSONObject(jsonTokener);
        } catch (JSONException e) {
            throw GeneralExceptionBuilder.buildJsonParsingErrorException(e);
        } catch (IOException e) {
            throw GeneralExceptionBuilder.buildIOErrorException(e);
        }
    }
}
