package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item addItem(Item item);

    Item updateItem(Item item);

    Optional<Item> getItemById(Long id);

    List<Item> getAllForSharer(Long sharerId);

    List<Item> getWithMatchInDescription(String searchText);
}
