package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.exception.ItemDtoMappingException;
import ru.practicum.shareit.item.model.Item;

public class ItemDtoMapper {

    public static ItemDto toDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailable());
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
        var item = new Item(-1L, sharerId);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public static Item updateItem(Item item, ItemDto update) {
        item.setName(update.getName() == null ? item.getName() : update.getName());
        item.setDescription(update.getDescription() == null ? item.getDescription() : update.getDescription());
        item.setAvailable(update.getAvailable() == null ? item.isAvailable() : update.getAvailable());
        return item;
    }
}
