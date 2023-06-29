package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.error.exception.InvalidEntityException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoMappingException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getAllItems() {
        return itemRepository.getAllItems().stream()
            .map(ItemDtoMapper::toDto)
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public ItemDto addItem(Long sharerId, ItemDto item) {
        if (!userRepository.indexExists(sharerId)) {
            throw new EntityNotFoundException("пользователь с таким id не найден");
        }
        try {
            return ItemDtoMapper.toDto(itemRepository.addItem(ItemDtoMapper.fromDto(sharerId, item)));
        } catch (ItemDtoMappingException e) {
            throw new InvalidEntityException(e.getMessage());
        }
    }

    @Override
    public ItemDto updateItem(Long sharerId, ItemDto item, Long itemId) {
        if (!userRepository.indexExists(sharerId)) {
            throw new EntityNotFoundException("пользователь с таким id не найден");
        }
        var original = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("предмет с таким id не найден"));

        if (!original.getSharerId().equals(sharerId)) {
            throw new EntityNotFoundException("пользователь не является владельцем предмета");
        }

        return ItemDtoMapper.toDto(itemRepository.updateItem(ItemDtoMapper.updateItem(original, item)));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        var item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("предмет с таким id не найден"));
        return ItemDtoMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getAllForSharer(Long sharerId) {
        if (!userRepository.indexExists(sharerId)) {
            throw new EntityNotFoundException("пользователь с таким id не найден");
        }
        return itemRepository.getAllForSharer(sharerId).stream()
                .map(ItemDtoMapper::toDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<ItemDto> search(String searchText) {
        if (searchText == null) {
            return Collections.emptyList();
        }
        var processedSearchText = searchText.trim().toLowerCase();
        if (processedSearchText.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.getWithMatchInDescription(processedSearchText).stream()
                .map(ItemDtoMapper::toDto)
                .collect(Collectors.toUnmodifiableList());
    }
}
