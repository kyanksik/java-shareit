package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService requestService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    private long requestorId;
    private long ownerId;

    @BeforeEach
    void setUp() {
        UserDto requestor = new UserDto();
        requestor.setName("Requestor");
        requestor.setEmail("req@ex.com");
        requestorId = userService.create(requestor).getId();

        UserDto owner = new UserDto();
        owner.setName("Owner");
        owner.setEmail("owner@ex.com");
        ownerId = userService.create(owner).getId();
    }

    private ItemRequestDto request(String description) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription(description);
        return dto;
    }

    @Test
    void createReturnsRequestWithCreatedTime() {
        ItemRequestDto created = requestService.create(requestorId, request("Нужна дрель"));
        assertThat(created.getId()).isNotNull();
        assertThat(created.getDescription()).isEqualTo("Нужна дрель");
        assertThat(created.getCreated()).isNotNull();
    }

    @Test
    void findByRequestorReturnsRequestsWithAnswers() {
        long requestId = requestService.create(requestorId, request("Нужна дрель")).getId();

        ItemDto item = new ItemDto();
        item.setName("Дрель");
        item.setDescription("мощная");
        item.setAvailable(true);
        item.setRequestId(requestId);
        itemService.create(ownerId, item);

        List<ItemRequestDto> requests = requestService.findByRequestor(requestorId);
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getItems()).hasSize(1);
        assertThat(requests.get(0).getItems().get(0).getName()).isEqualTo("Дрель");
        assertThat(requests.get(0).getItems().get(0).getOwnerId()).isEqualTo(ownerId);
    }

    @Test
    void findAllReturnsOtherUsersRequests() {
        requestService.create(requestorId, request("Чужой запрос"));
        List<ItemRequestDto> forOwner = requestService.findAll(ownerId);
        assertThat(forOwner).hasSize(1);
        List<ItemRequestDto> forRequestor = requestService.findAll(requestorId);
        assertThat(forRequestor).isEmpty();
    }

    @Test
    void findByIdReturnsRequestWithAnswers() {
        long requestId = requestService.create(requestorId, request("Нужна пила")).getId();
        ItemDto item = new ItemDto();
        item.setName("Пила");
        item.setDescription("острая");
        item.setAvailable(true);
        item.setRequestId(requestId);
        itemService.create(ownerId, item);

        ItemRequestDto found = requestService.findById(ownerId, requestId);
        assertThat(found.getItems()).hasSize(1);
        assertThat(found.getItems().get(0).getName()).isEqualTo("Пила");
    }

    @Test
    void createForUnknownUserThrowsNotFound() {
        assertThrows(NotFoundException.class, () -> requestService.create(9999L, request("x")));
    }

    @Test
    void findByIdUnknownThrowsNotFound() {
        assertThrows(NotFoundException.class, () -> requestService.findById(requestorId, 9999L));
    }
}
