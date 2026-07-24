package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;

    private BookingDto booking() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStatus(BookingStatus.WAITING);
        return dto;
    }

    private BookingCreateDto createDto() {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        return dto;
    }

    @Test
    void create() throws Exception {
        when(bookingService.create(eq(1L), any())).thenReturn(booking());
        mvc.perform(post("/bookings")
                        .header(USER_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approve() throws Exception {
        BookingDto approved = booking();
        approved.setStatus(BookingStatus.APPROVED);
        when(bookingService.approve(1L, 1L, true)).thenReturn(approved);
        mvc.perform(patch("/bookings/1").header(USER_HEADER, 1).param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void approveByNotOwnerIsForbidden() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), org.mockito.ArgumentMatchers.anyBoolean()))
                .thenThrow(new ForbiddenException("нельзя"));
        mvc.perform(patch("/bookings/1").header(USER_HEADER, 2).param("approved", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    void findById() throws Exception {
        when(bookingService.findById(1L, 1L)).thenReturn(booking());
        mvc.perform(get("/bookings/1").header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findByBooker() throws Exception {
        when(bookingService.findByBooker(1L, BookingState.ALL)).thenReturn(List.of(booking()));
        mvc.perform(get("/bookings").header(USER_HEADER, 1).param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void findByOwner() throws Exception {
        when(bookingService.findByOwner(1L, BookingState.ALL)).thenReturn(List.of(booking()));
        mvc.perform(get("/bookings/owner").header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
