package ru.practicum.shareit.request.dto;

import lombok.Value;

@Value
public class ItemRequestReplyDto {
    Long id;
    String name;
    String description;
    Long requestId;
    boolean available;
}
