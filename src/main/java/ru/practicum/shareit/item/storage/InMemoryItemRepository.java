package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private long nextId = 1L;

    @Override
    public List<Item> getAllItems() {
        return null;
    }

    @Override
    public Item addItem(Item item) {
        item.setId(nextId);
        items.put(nextId, item);
        nextId++;
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getAllForSharer(Long sharerId) {
        return items.values().stream()
                .filter(i -> i.getSharerId().equals(sharerId))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Item> getWithMatchInDescription(String searchText) {
        return items.values().stream()
                .filter(i -> i.isAvailable() &&  i.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toUnmodifiableList());
    }
}
