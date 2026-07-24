package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> bookingJson;
    @Autowired
    private JacksonTester<BookingCreateDto> createJson;

    @Test
    void serializeBookingDto() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStatus(BookingStatus.APPROVED);
        dto.setStart(LocalDateTime.of(2030, 1, 1, 12, 0, 0));
        dto.setEnd(LocalDateTime.of(2030, 1, 2, 12, 0, 0));

        var result = bookingJson.write(dto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2030-01-01T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2030-01-02T12:00:00");
    }

    @Test
    void deserializeBookingCreateDto() throws Exception {
        String content = "{\"itemId\":5,\"start\":\"2030-01-01T12:00:00\",\"end\":\"2030-01-02T12:00:00\"}";
        BookingCreateDto dto = createJson.parseObject(content);
        assertThat(dto.getItemId()).isEqualTo(5L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2030, 1, 1, 12, 0, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2030, 1, 2, 12, 0, 0));
    }
}
