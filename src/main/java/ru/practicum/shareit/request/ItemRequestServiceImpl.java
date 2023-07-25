package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.error.exception.InvalidEntityException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.ItemRequestReplyDto;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto addRequest(Long userId, String requestText) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        if (requestText.isBlank()) {
            throw new InvalidEntityException("текст запроса не может быть пустым");
        }
        ItemRequest request = new ItemRequest();
        request.setRequesterId(userId);
        request.setDescription(requestText);
        return ItemRequestDtoMapper.toDto(itemRequestRepository.save(request));
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        return itemRequestRepository.findByRequesterId(userId)
                .stream()
                .map(r -> {
                    var dto = ItemRequestDtoMapper.toDto(r);
                    dto.setReplies(getReplies(r.getId()));
                    return dto;
                })
                .collect(Collectors.toUnmodifiableList());
    }

    private List<ItemRequestReplyDto> getReplies(Long requestId) {
        return itemRepository.findByRequestId(requestId)
                .stream()
                .map(i -> new ItemRequestReplyDto(
                        i.getId(),
                        i.getName(),
                        i.getDescription(),
                        requestId,
                        i.isAvailable()))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemRequestRepository.findAll(page)
                .stream()
                .map(r -> {
                    var dto = ItemRequestDtoMapper.toDto(r);
                    dto.setReplies(getReplies(r.getId()));
                    return dto;
                })
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public ItemRequestDto getRequestById(Long id) {
        var dto = ItemRequestDtoMapper.toDto(
                itemRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("запрос с id [%d]", id)))
        );
        dto.setReplies(getReplies(id));
        return dto;
    }
}
