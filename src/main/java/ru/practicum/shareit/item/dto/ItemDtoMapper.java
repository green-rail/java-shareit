package ru.practicum.shareit.item.dto;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.exception.ItemDtoMappingException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Slf4j
public class ItemDtoMapper {

    public static ItemDto toDto(Item item, List<CommentDto> comments, BookingDto last, BookingDto next) {
        return new ItemDto(item.getId(), item.getName(),
                item.getDescription(), item.isAvailable(),last, next, comments);
    }

    public  static Item fromDto(Long sharerId, ItemDto itemDto) throws ItemDtoMappingException {
        if (sharerId == null || sharerId < 0) {
            throw new ItemDtoMappingException("неверный индекс владельца");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ItemDtoMappingException("имя предмета не может быть пустым");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ItemDtoMappingException("название предмета не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            throw new ItemDtoMappingException("доступность предмета должна быть указана");
        }

        return new Item(
                itemDto.getId(),
                sharerId,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null
        );
    }

    public static Item updateItem(Item item, ItemDto update) {
        item.setName(update.getName() == null ? item.getName() : update.getName());
        item.setDescription(update.getDescription() == null ? item.getDescription() : update.getDescription());
        item.setAvailable(update.getAvailable() == null ? item.isAvailable() : update.getAvailable());
        log.debug("updating an item: " + item);
        log.debug("with values: " + update);
        return item;
    }
}
