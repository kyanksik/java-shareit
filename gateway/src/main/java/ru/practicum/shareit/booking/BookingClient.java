package ru.practicum.shareit.booking;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.client.BaseClient;

/**
 * Клиент gateway для обращения к эндпоинтам бронирований shareit-server.
 */
@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    /**
     * Создаёт запрос на бронирование.
     */
    public ResponseEntity<Object> create(long userId, BookItemRequestDto dto) {
        return post("", userId, dto);
    }

    /**
     * Подтверждает или отклоняет бронирование.
     */
    public ResponseEntity<Object> approve(long userId, long bookingId, boolean approved) {
        return patch("/" + bookingId + "?approved={approved}", userId, Map.of("approved", approved), null);
    }

    /**
     * Запрашивает бронирование по идентификатору.
     */
    public ResponseEntity<Object> getById(long userId, long bookingId) {
        return get("/" + bookingId, userId);
    }

    /**
     * Запрашивает бронирования пользователя по состоянию.
     */
    public ResponseEntity<Object> getByBooker(long userId, String state) {
        return get("?state={state}", userId, Map.of("state", state));
    }

    /**
     * Запрашивает бронирования вещей владельца по состоянию.
     */
    public ResponseEntity<Object> getByOwner(long userId, String state) {
        return get("/owner?state={state}", userId, Map.of("state", state));
    }
}
