package ru.practicum.shareit.request.dto;

import lombok.Value;

@Value
public class ItemRequestReplyDto {
    Long itemId;
    String itemName;
    String itemDescription;
    Long requestId;
    boolean available;
}
