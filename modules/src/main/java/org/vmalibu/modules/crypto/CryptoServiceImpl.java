package org.vmalibu.modules.crypto;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class CryptoServiceImpl implements CryptoService {

    @Override
    public byte[] encode(byte[] value) {
        return value;
    }

    @Override
    public byte[] encode(String value) {
        if (value == null) {
            return null;
        }

        return encode(value.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public byte[] decode(byte[] value) {
        return value;
    }

    @Override
    public String decodeAsString(byte[] value) {
        if (value == null) {
            return null;
        }

        return new String(decode(value));
    }
}
