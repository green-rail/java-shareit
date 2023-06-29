package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Long sharerId) {
        return itemService.addItem(sharerId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto item,
                              @RequestHeader("X-Sharer-User-Id") Long sharerId,
                              @PathVariable Long itemId) {
        return itemService.updateItem(sharerId, item, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long sharerId,
                           @PathVariable Long itemId) {

        return itemService.getItemById(itemId);

    }

    @GetMapping
    public List<ItemDto> getItemsForSharer(@RequestHeader("X-Sharer-User-Id") Long sharerId) {
        return itemService.getAllForSharer(sharerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.search(text);
    }
}
