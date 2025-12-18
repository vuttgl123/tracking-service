package marketing.tracking_service.tracking.infrastructure.persistence.converter;

import jakarta.persistence.AttributeConverter;

import java.util.Locale;

public abstract class LowercaseEnumConverter<E extends Enum<E>> implements AttributeConverter<E, String> {

    private final Class<E> enumClass;

    protected LowercaseEnumConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public String convertToDatabaseColumn(E attribute) {
        return attribute == null ? null : attribute.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public E convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Enum.valueOf(enumClass, dbData.toUpperCase(Locale.ROOT));
    }
}
