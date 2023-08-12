package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.common.NormalizedPageRequest;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.error.exception.InvalidEntityException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.exception.InvalidCommentAuthorException;
import ru.practicum.shareit.item.exception.ItemDtoMappingException;
import ru.practicum.shareit.item.exception.OwnerMismatchException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<ItemDto> getAllForSharer(Long sharerId, int from, int size) {
        if (!userRepository.existsById(sharerId)) {
            throw new UserNotFoundException(sharerId);
        }
        List<ItemDto> result = new ArrayList<>();
        PageRequest page = new NormalizedPageRequest(from, size);
        for (Item item: itemRepository.findAllBySharerId(sharerId, page)) {
            var bookings = findLastAndNextBooking(item);
            List<CommentDto> comments = commentRepository.findByItemId(item.getId())
                    .stream()
                    .map(c -> CommentDtoMapper.toDto(c, c.getAuthor().getName()))
                    .collect(Collectors.toUnmodifiableList());
            result.add(ItemDtoMapper.toDto(item, comments, bookings[0], bookings[1]));
        }
        result.sort(Comparator.comparing(ItemDto::getId));
        return result;
    }

    @Transactional(readOnly = true)
    private BookingDto[] findLastAndNextBooking(Item item) {

        List<Booking> bookings = bookingRepository.findByItemOrderByStartAsc(item);
        System.out.println("---NOW from date time to inst: " + LocalDateTime.now().toInstant(ZoneOffset.UTC));
        System.out.println("-----------NOW from date time: " + LocalDateTime.now());
        bookings.forEach(b -> System.out.println(b.getStart()));
        //Instant now = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        //Instant now = Instant.now();
        var now = LocalDateTime.now();

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
    @Transactional(readOnly = true)
    public List<ItemDto> search(String searchText, int from, int size) {
        if (searchText == null || searchText.isBlank()) {
            return Collections.emptyList();
        }
        PageRequest page = new NormalizedPageRequest(from, size);
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
                //.anyMatch(b -> b.getEnd().isBefore(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
                .anyMatch(b -> b.getEnd().isBefore(LocalDateTime.now()));
        if (!anyComplete) {
            throw new InvalidCommentAuthorException(
                    String.format("у пользователя [%d] нет законченной аренды предмета [%d]", userId, itemId));
        }
        var comment = new Comment();
        comment.setAuthor(user);
        comment.setItemId(itemId);
        comment.setCommentText(commentDto.getText());
        return CommentDtoMapper.toDto(commentRepository.save(comment), user.getName());
    }
}
