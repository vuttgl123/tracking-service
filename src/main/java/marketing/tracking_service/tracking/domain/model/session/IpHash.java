package marketing.tracking_service.tracking.domain.model.session;

import marketing.tracking_service.shared.domain.ValueObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

public record IpHash(String value) implements ValueObject {

    private static final int SHA256_LENGTH = 64;
    private static final String ALGORITHM = "SHA-256";

    public IpHash {
        Objects.requireNonNull(value, "IP hash cannot be null");
        if (value.length() != SHA256_LENGTH) {
            throw new IllegalArgumentException("Invalid IP hash length");
        }
    }

    public static IpHash fromIpAddress(String ipAddress) {
        Objects.requireNonNull(ipAddress, "IP address cannot be null");

        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = digest.digest(ipAddress.getBytes(StandardCharsets.UTF_8));
            String hexHash = HexFormat.of().formatHex(hash);
            return new IpHash(hexHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public static IpHash from(String hash) {
        return new IpHash(hash);
    }

    @Override
    public String toString() {
        return value;
    }
}