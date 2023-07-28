package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.common.Util;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.error.exception.InvalidEntityException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exception.InvalidCommentAuthorException;
import ru.practicum.shareit.item.exception.ItemDtoMappingException;
import ru.practicum.shareit.item.exception.OwnerMismatchException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(Long sharerId, ItemDto itemDto) {
        Item item;
        try {
            item = ItemDtoMapper.fromDto(sharerId, itemDto);
        } catch (ItemDtoMappingException e) {
            throw new InvalidEntityException(e.getMessage());
        }
        if (!userRepository.existsById(sharerId)) {
            throw new UserNotFoundException(sharerId);
        }
        return ItemDtoMapper.toDto(itemRepository.save(item), null, null, null);
    }

    @Override
    public ItemDto updateItem(Long sharerId, ItemDto item, Long itemId) {
        if (!userRepository.existsById(sharerId)) {
            throw new UserNotFoundException(sharerId);
        }
        var original = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("предмет с id [%d] не найден", itemId)));

        if (!original.getSharerId().equals(sharerId)) {
            throw new OwnerMismatchException(
                    String.format("пользователь [%d] не является владельцем предмета [%d]", sharerId, itemId));
        }

        return ItemDtoMapper.toDto(itemRepository.save(ItemDtoMapper.updateItem(original, item)),
                null, null, null);
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        var item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("предмет с id [%d] не найден", itemId)));

        var comments = commentRepository.findByItemId(item.getId())
                .stream()
                .map(c -> CommentDtoMapper.toDto(c, c.getAuthor().getName()))
                .collect(Collectors.toUnmodifiableList());

        if (!userId.equals(item.getSharerId())) return ItemDtoMapper.toDto(item, comments, null, null);

        var lastAndNextBookings = findLastAndNextBooking(item);

        return ItemDtoMapper.toDto(item, comments, lastAndNextBookings[0], lastAndNextBookings[1]);
    }

    @Override
    public List<ItemDto> getAllForSharer(Long sharerId, int from, int size) {
        Util.checkPageRequestBoundaries(from, size);
        if (!userRepository.existsById(sharerId)) {
            throw new UserNotFoundException(sharerId);
        }
        List<ItemDto> result = new ArrayList<>();
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        for (Item item: itemRepository.findAllBySharerId(sharerId, page)) {
            var bookings = findLastAndNextBooking(item);
            List<CommentDto> comments = commentRepository.findByItemId(item.getId())
                    .stream()
                    .map(c -> CommentDtoMapper.toDto(c, c.getAuthor().getName()))
                    .collect(Collectors.toUnmodifiableList());
            result.add(ItemDtoMapper.toDto(item, comments, bookings[0], bookings[1]));
        }
        return result;
    }

    private BookingDto[] findLastAndNextBooking(Item item) {

        List<Booking> bookings = bookingRepository.findByItemOrderByStartAsc(item);
        Instant now = LocalDateTime.now().toInstant(ZoneOffset.UTC);

        BookingDto last = bookings.stream()
                .filter(b -> b.getStart().isBefore(now) && !b.getStatus().equals(BookingStatus.REJECTED))
                .max(Comparator.comparing(Booking::getStart))
                .map(b -> BookingDtoMapper.toDto(b, null, null))
                .orElse(null);

        BookingDto next = bookings.stream()
                .filter(b -> b.getStart().isAfter(now) && !b.getStatus().equals(BookingStatus.REJECTED))
                .min(Comparator.comparing(Booking::getStart))
                .map(b -> BookingDtoMapper.toDto(b, null, null))
                .orElse(null);

        return new BookingDto[] {last, next};
    }

    @Override
    public List<ItemDto> search(String searchText, int from, int size) {
        Util.checkPageRequestBoundaries(from, size);
        if (searchText == null || searchText.isBlank()) {
            return Collections.emptyList();
        }
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemRepository
                .findByAvailableTrueAndDescriptionContainingIgnoreCase(searchText.trim().toLowerCase(), page)
                .stream()
                .map(i -> ItemDtoMapper.toDto(i, null, null, null))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        var user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        if (!itemRepository.existsById(itemId)) {
            throw new EntityNotFoundException(String.format("предмет с id [%d] не найден", itemId));
        }
        var bookings = bookingRepository.findByBookerIdAndItemId(userId, itemId);
        boolean anyComplete = bookings.stream()
                .anyMatch(b -> b.getEnd().isBefore(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
        if (!anyComplete) {
            throw new InvalidCommentAuthorException(
                    String.format("у пользователя [%d] нет законченной аренды предмета [%d]", userId, itemId));
        }
        var comment = new Comment();
        comment.setAuthor(user);
        comment.setItemId(itemId);
        comment.setCommentText(commentDto.getText());
        comment.setCreated(Instant.now());
        return CommentDtoMapper.toDto(commentRepository.save(comment), user.getName());
    }
}
