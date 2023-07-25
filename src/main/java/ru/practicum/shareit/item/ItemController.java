package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto item,
                           @RequestHeader("X-Sharer-User-Id") Long sharerId,
                           HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());
        return itemService.addItem(sharerId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto item,
                              @RequestHeader("X-Sharer-User-Id") Long sharerId,
                              @PathVariable Long itemId,
                              HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());
        return itemService.updateItem(sharerId, item, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long sharerId,
                           @PathVariable Long itemId,
                           HttpServletRequest request) {

        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());
        return itemService.getItemById(sharerId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsForSharer(@RequestHeader("X-Sharer-User-Id") Long sharerId,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size,
                                           HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());
        return itemService.getAllForSharer(sharerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size,
                                    HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long sharerId,
                                    @PathVariable Long itemId,
                                    @Valid @RequestBody CommentDto comment,
                                    HttpServletRequest request) {

        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());
        return itemService.addComment(sharerId, itemId, comment);
    }


}
