package marketing.tracking_service.shared.domain;

abstract class AbstractValueObject implements ValueObject {
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        @SuppressWarnings("unchecked")
        AbstractValueObject other = (AbstractValueObject) obj;
        return equalsByValue(other);
    }

    protected abstract boolean equalsByValue(AbstractValueObject other);

    @Override
    public final int hashCode() {
        return hashCodeByValue();
    }

    protected abstract int hashCodeByValue();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
    }
}
