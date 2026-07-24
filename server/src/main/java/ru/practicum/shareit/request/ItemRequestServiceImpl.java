package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private static final Sort CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");

    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestDto create(long userId, ItemRequestDto dto) {
        User requestor = getUserOrThrow(userId);
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<ItemRequestDto> findByRequestor(long userId) {
        getUserOrThrow(userId);
        return withAnswers(requestRepository.findByRequestorId(userId, CREATED_DESC));
    }

    @Override
    public List<ItemRequestDto> findAll(long userId) {
        getUserOrThrow(userId);
        return withAnswers(requestRepository.findByRequestorIdNot(userId, CREATED_DESC));
    }

    @Override
    public ItemRequestDto findById(long userId, long requestId) {
        getUserOrThrow(userId);
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));
        return ItemRequestMapper.toDto(request, itemRepository.findByRequestId(requestId));
    }

    private List<ItemRequestDto> withAnswers(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream().map(ItemRequest::getId).toList();
        Map<Long, List<Item>> answersByRequest = itemRepository.findByRequestIdIn(requestIds).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
        return requests.stream()
                .map(request -> ItemRequestMapper.toDto(request,
                        answersByRequest.getOrDefault(request.getId(), List.of())))
                .toList();
    }

    private User getUserOrThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }
}
