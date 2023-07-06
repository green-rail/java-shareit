package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.error.exception.InvalidEntityException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.exception.ItemDtoMappingException;
import ru.practicum.shareit.item.exception.OwnerMismatchException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(Long sharerId, ItemDto item) {
        if (!userRepository.existsById(sharerId)) {
            throw new UserNotFoundException(sharerId);
        }
        try {
            return ItemDtoMapper.toDto(itemRepository.save(ItemDtoMapper.fromDto(sharerId, item)));
        } catch (ItemDtoMappingException e) {
            throw new InvalidEntityException(e.getMessage());
        }
    }

    @Override
    public ItemDto updateItem(Long sharerId, ItemDto item, Long itemId) {
        if (!userRepository.existsById(sharerId)) {
            throw new UserNotFoundException(sharerId);
        }
        var original = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("предмет с id [%d] не найден", itemId)));

        if (!original.getSharerId().equals(sharerId)) {
            throw new OwnerMismatchException(
                    String.format("пользователь [%d] не является владельцем предмета [%d]", sharerId, itemId));
        }

        return ItemDtoMapper.toDto(itemRepository.save(ItemDtoMapper.updateItem(original, item)));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        var item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("предмет с id [%d] не найден", itemId)));
        return ItemDtoMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getAllForSharer(Long sharerId) {
        if (!userRepository.existsById(sharerId)) {
            throw new UserNotFoundException(sharerId);
        }
        return itemRepository.findBySharerId(sharerId).stream()
                .map(ItemDtoMapper::toDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<ItemDto> search(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findByAvailableTrueAndDescriptionContainingIgnoreCase(searchText.trim().toLowerCase())
                .stream()
                .map(ItemDtoMapper::toDto)
                .collect(Collectors.toUnmodifiableList());
    }
}
