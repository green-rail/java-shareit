package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestDtoMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("yyyy.MM.dd hh:mm:ss")
            .withZone(ZoneOffset.UTC);

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                //formatter.format(itemRequest.getCreated()),
                itemRequest.getCreated(),
                null);
    }

    public static List<ItemRequestDto> toDto(Collection<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(ItemRequestDtoMapper::toDto)
                .collect(Collectors.toUnmodifiableList());
    }

}
