package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long sharerId, ItemDto item);

    ItemDto updateItem(Long sharerId, ItemDto item, Long itemId);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> getAllForSharer(Long sharerId, int from, int size);

    List<ItemDto> search(String text, int from, int size);

    CommentDto addComment(Long userId, Long itemId, CommentDto comment);
}
