package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    void handleNotFound() {
        assertThat(handler.handleNotFound(new NotFoundException("нет")).getError()).isEqualTo("нет");
    }

    @Test
    void handleValidation() {
        assertThat(handler.handleValidation(new ValidationException("плохо")).getError()).isEqualTo("плохо");
    }

    @Test
    void handleConflict() {
        assertThat(handler.handleConflict(new ConflictException("конфликт")).getError()).isEqualTo("конфликт");
    }

    @Test
    void handleForbidden() {
        assertThat(handler.handleForbidden(new ForbiddenException("нельзя")).getError()).isEqualTo("нельзя");
    }

    @Test
    void handleDataIntegrity() {
        ErrorResponse response = handler.handleDataIntegrity(new DataIntegrityViolationException("boom"));
        assertThat(response.getError()).contains("целостности");
    }

    @Test
    void handleMethodArgumentNotValid() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getMessage()).thenReturn("bad field");
        assertThat(handler.handleMethodArgumentNotValid(ex).getError()).contains("bad field");
    }

    @Test
    void handleMissingHeader() {
        MissingRequestHeaderException ex = mock(MissingRequestHeaderException.class);
        when(ex.getHeaderName()).thenReturn("X-Sharer-User-Id");
        assertThat(handler.handleMissingHeader(ex).getError()).contains("X-Sharer-User-Id");
    }

    @Test
    void handleThrowable() {
        ErrorResponse response = handler.handleThrowable(new RuntimeException("oops"));
        assertThat(response.getError()).contains("oops");
    }
}
