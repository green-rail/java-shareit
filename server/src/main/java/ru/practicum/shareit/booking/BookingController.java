package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.common.Constants.userIdRequestHeaderName;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader(userIdRequestHeaderName) Long bookerId,
                                 @Valid @RequestBody BookingDto booking,
                                 HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());
        log.debug("booking start from server {}", booking.getStart());
        return bookingService.addBooking(bookerId, booking);
    }

    @PatchMapping(value = "/{bookingId}", params = "approved")
    public BookingDto approveBooking(@RequestHeader(userIdRequestHeaderName) Long sharerId,
                                     @PathVariable Long bookingId,
                                     @RequestParam boolean approved,
                                     HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());
        return bookingService.approveBooking(sharerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(userIdRequestHeaderName) Long sharerId,
                                 @PathVariable Long bookingId,
                                 HttpServletRequest request) {

        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());
        return bookingService.getBooking(sharerId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader(userIdRequestHeaderName) Long sharerId,
                                            @RequestParam(defaultValue = "ALL") BookingState state,
                                            @RequestParam(defaultValue = "0")  @PositiveOrZero int from,
                                            @RequestParam(defaultValue = "10") @Positive int size,
                                            HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return bookingService.getUserBookings(sharerId, state, from, size);
    }

    @GetMapping(value = "/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader(userIdRequestHeaderName) Long sharerId,
                                             @RequestParam(defaultValue = "ALL") BookingState state,
                                             @RequestParam(defaultValue = "0")  @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size,
                                             HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return bookingService.getOwnerBookings(sharerId, state, from, size);
    }
}
