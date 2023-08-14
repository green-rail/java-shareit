package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.common.Defaults.X_SHARER_HEADER_NAME;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {


    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(X_SHARER_HEADER_NAME) @Positive long userId,
                                             @Valid @RequestBody ItemRequestDto requestDto) {

        log.info("Add request userId={}, request={}", userId, requestDto);
        return requestClient.addRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(X_SHARER_HEADER_NAME) @Positive long userId) {

        log.info("Get user requests userId={}", userId);
        return requestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(X_SHARER_HEADER_NAME) @Positive long userId,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {

        log.info("Get all requests userId={}, from={}, size={}", userId, from, size);
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRequest(@RequestHeader(X_SHARER_HEADER_NAME) @Positive long userId,
                                             @PathVariable @PositiveOrZero long id) {

        log.info("Get request userId={}, id={}", userId, id);
        return requestClient.getRequestById(userId, id);
    }
}
