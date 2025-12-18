package marketing.tracking_service.shared.infrastructure.id;

import marketing.tracking_service.shared.domain.TimeBasedIdGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.Objects;

@Component
public class UlidGenerator implements TimeBasedIdGenerator {
    private static final char[] CROCKFORD = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();
    private static final int ULID_LENGTH = 26;
    private static final int TIMESTAMP_LENGTH = 10;

    private final SecureRandom random = new SecureRandom();
    private final Clock clock;

    public UlidGenerator(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public String newId() {
        long time = clock.instant().toEpochMilli();
        byte[] bytes = new byte[16];

        bytes[0] = (byte) (time >>> 40);
        bytes[1] = (byte) (time >>> 32);
        bytes[2] = (byte) (time >>> 24);
        bytes[3] = (byte) (time >>> 16);
        bytes[4] = (byte) (time >>> 8);
        bytes[5] = (byte) (time);

        byte[] rnd = new byte[10];
        random.nextBytes(rnd);
        System.arraycopy(rnd, 0, bytes, 6, 10);

        return encodeBase32(bytes);
    }

    @Override
    public long extractTimestamp(String id) {
        if (!isValid(id)) {
            throw new IllegalArgumentException("Invalid ULID format");
        }

        String timestampPart = id.substring(0, TIMESTAMP_LENGTH);
        long timestamp = 0;

        for (int i = 0; i < TIMESTAMP_LENGTH; i++) {
            char c = timestampPart.charAt(i);
            int value = decodeChar(c);
            timestamp = (timestamp << 5) | value;
        }

        return timestamp;
    }

    @Override
    public boolean isValid(String id) {
        if (id == null || id.length() != ULID_LENGTH) {
            return false;
        }

        for (char c : id.toCharArray()) {
            if (decodeChar(c) == -1) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getStrategy() {
        return "ULID";
    }

    private static String encodeBase32(byte[] data) {
        StringBuilder sb = new StringBuilder(26);
        int index = 0;
        int curr = 0;
        int bits = 0;

        while (sb.length() < 26) {
            if (bits < 5) {
                if (index < data.length) {
                    curr = (curr << 8) | (data[index++] & 0xFF);
                    bits += 8;
                } else {
                    curr <<= (5 - bits);
                    bits = 5;
                }
            }
            int val = (curr >>> (bits - 5)) & 0x1F;
            bits -= 5;
            sb.append(CROCKFORD[val]);
        }
        return sb.toString();
    }

    private static int decodeChar(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'A' && c <= 'H') {
            return c - 'A' + 10;
        }
        if (c >= 'J' && c <= 'K') {
            return c - 'J' + 18;
        }
        if (c >= 'M' && c <= 'N') {
            return c - 'M' + 20;
        }
        if (c >= 'P' && c <= 'T') {
            return c - 'P' + 22;
        }
        if (c >= 'V' && c <= 'Z') {
            return c - 'V' + 27;
        }
        return -1;
    }
}