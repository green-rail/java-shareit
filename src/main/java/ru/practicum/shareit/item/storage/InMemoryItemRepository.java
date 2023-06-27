package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    @Override
    public List<Item> getAllItems() {
        return null;
    }
}
