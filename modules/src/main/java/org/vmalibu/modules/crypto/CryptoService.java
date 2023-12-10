package org.vmalibu.modules.crypto;

public interface CryptoService {

    byte[] encode(byte[] value);

    byte[] encode(String value);

    byte[] decode(byte[] value);

    String decodeAsString(byte[] value);
}
