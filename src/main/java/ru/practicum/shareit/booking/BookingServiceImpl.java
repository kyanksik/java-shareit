package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private static final Sort START_DESC = Sort.by(Sort.Direction.DESC, "start");

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDto create(long userId, BookingCreateDto dto) {
        User booker = getUserOrThrow(userId);
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id " + dto.getItemId() + " не найдена"));

        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new ValidationException("Вещь с id " + item.getId() + " недоступна для бронирования");
        }
        if (item.getOwner() != null && Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Владелец не может забронировать свою вещь");
        }
        if (!dto.getEnd().isAfter(dto.getStart())) {
            throw new ValidationException("Дата окончания должна быть позже даты начала бронирования");
        }

        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approve(long userId, long bookingId, boolean approved) {
        Booking booking = getBookingOrThrow(bookingId);
        Item item = booking.getItem();
        if (item.getOwner() == null || !Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Пользователь " + userId
                    + " не является владельцем вещи и не может подтверждать бронирование");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже обработано, текущий статус: " + booking.getStatus());
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(long userId, long bookingId) {
        Booking booking = getBookingOrThrow(bookingId);
        boolean isBooker = Objects.equals(booking.getBooker().getId(), userId);
        boolean isOwner = booking.getItem().getOwner() != null
                && Objects.equals(booking.getItem().getOwner().getId(), userId);
        if (!isBooker && !isOwner) {
            throw new NotFoundException("Бронирование с id " + bookingId
                    + " недоступно пользователю " + userId);
        }
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> findByBooker(long userId, BookingState state) {
        getUserOrThrow(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBookerId(userId, START_DESC);
            case CURRENT -> bookingRepository
                    .findByBookerIdAndStartBeforeAndEndAfter(userId, now, now, START_DESC);
            case PAST -> bookingRepository.findByBookerIdAndEndBefore(userId, now, START_DESC);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfter(userId, now, START_DESC);
            case WAITING -> bookingRepository
                    .findByBookerIdAndStatus(userId, BookingStatus.WAITING, START_DESC);
            case REJECTED -> bookingRepository
                    .findByBookerIdAndStatus(userId, BookingStatus.REJECTED, START_DESC);
        };
        return toDtoList(bookings);
    }

    @Override
    public List<BookingDto> findByOwner(long userId, BookingState state) {
        getUserOrThrow(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByItemOwnerId(userId, START_DESC);
            case CURRENT -> bookingRepository
                    .findByItemOwnerIdAndStartBeforeAndEndAfter(userId, now, now, START_DESC);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndBefore(userId, now, START_DESC);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartAfter(userId, now, START_DESC);
            case WAITING -> bookingRepository
                    .findByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, START_DESC);
            case REJECTED -> bookingRepository
                    .findByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, START_DESC);
        };
        return toDtoList(bookings);
    }

    private List<BookingDto> toDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    private Booking getBookingOrThrow(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
    }

    private User getUserOrThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }
}
