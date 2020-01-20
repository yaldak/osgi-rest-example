package cc.kako.examples.rest.api.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Objects;

@Converter(autoApply = true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, Timestamp> {
    @Override
    public LocalDate convertToEntityAttribute(final Timestamp sqlTimestamp) {
        return Objects.nonNull(sqlTimestamp) ? sqlTimestamp.toLocalDateTime().toLocalDate() : null;
    }

    @Override
    public Timestamp convertToDatabaseColumn(final LocalDate localDate) {
        return Objects.nonNull(localDate) ? Timestamp.valueOf(localDate.atTime(0, 0)) : null;
    }
}
