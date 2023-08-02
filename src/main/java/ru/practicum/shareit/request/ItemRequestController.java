package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.common.Constants.userIdRequestHeaderName;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestBody ItemRequestDto requestDto,
                                     @RequestHeader(userIdRequestHeaderName) Long userId,
                                     HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return itemRequestService.addRequest(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(userIdRequestHeaderName) Long userId,
                                                HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(userIdRequestHeaderName)  Long userId,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(defaultValue = "10") @Positive int size,
                                               HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return itemRequestService.getAllRequests(from, size, userId);
    }


    @GetMapping("/{id}")
    public ItemRequestDto getRequest(@PathVariable Long id,
                                     @RequestHeader(userIdRequestHeaderName) Long userId,
                                     HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return itemRequestService.getRequestById(userId, id);
    }
}
