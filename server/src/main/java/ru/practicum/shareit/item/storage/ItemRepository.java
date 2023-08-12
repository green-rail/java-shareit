package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllBySharerId(Long sharerId, Pageable page);

    Page<Item> findByAvailableTrueAndDescriptionContainingIgnoreCase(String searchText, Pageable page);

    List<Item> findByRequestId(Long requestId);
}
