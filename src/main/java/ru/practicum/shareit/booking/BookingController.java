package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@Valid @RequestBody BookingDto booking, HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());
        return bookingService.addBooking(booking);
    }

    @PatchMapping(value = "/{bookingId}", params = "approved")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long sharerId,
                                     @PathVariable Long bookingId,
                                     @RequestParam boolean approved,
                                     HttpServletRequest request ) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());
        return bookingService.approveBooking(sharerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long sharerId,
                                 @PathVariable Long bookingId,
                                 HttpServletRequest request ) {

        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());
        return bookingService.getBooking(sharerId, bookingId);

    }

    @GetMapping(params = "state")
    public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long sharerId,
                                            @RequestParam(required = false) BookingState state,
                                            HttpServletRequest request ) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return bookingService.getUserBookings(sharerId, state == null ? BookingState.ALL : state);
    }

    @GetMapping(value = "/owner", params = "state")
    public List<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long sharerId,
                                                     @RequestParam(required = false) BookingState state,
                                                     HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return bookingService.getOwnerBookings(sharerId, state == null ? BookingState.ALL : state);

    }
}
