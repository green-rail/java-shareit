package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class ItemRequestDto {
    private final Long id;
    private final String description;
    private final String created;
    private List<ItemRequestReplyDto> replies;
}
