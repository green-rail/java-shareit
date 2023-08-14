package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.common.Defaults.X_SHARER_HEADER_NAME;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(X_SHARER_HEADER_NAME) @Positive long userId,
                                          @RequestBody @Valid ItemDto itemDto) {
        log.info("Add item item={}, userId={}", itemDto, userId);
        if (!itemDto.canCreate()) {
            throw new IllegalArgumentException();
        }
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(X_SHARER_HEADER_NAME) @Positive long userId,
                                             @RequestBody @Valid ItemDto itemDto,
                                             @PathVariable long itemId) {
        log.info("Update item item={}, userId={}, itemId={}", itemDto, userId, itemId);
        if (!itemDto.canUpdate()) {
            throw new IllegalArgumentException();
        }
        return itemClient.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(X_SHARER_HEADER_NAME) @Positive Long userId,
                                          @PathVariable @Positive long itemId) {

        log.info("Get item userId={}, itemId={}", userId, itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsForSharer(@RequestHeader(X_SHARER_HEADER_NAME) @Positive long userId,
                                                    @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get items for sharer  userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAllForSharer(userId, from, size);
    }


    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(X_SHARER_HEADER_NAME) @Positive long userId,
                                             @RequestParam String text,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                             @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Search  text={}, from={}, size={}", text, from, size);
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(X_SHARER_HEADER_NAME) @Positive long userId,
                                             @PathVariable @Positive long itemId,
                                             @Valid @RequestBody CommentDto comment) {

        log.info("Add comment userId={}, itemId={}, comment={}", userId, itemId, comment);
        return itemClient.addComment(userId, itemId, comment);
    }
}
