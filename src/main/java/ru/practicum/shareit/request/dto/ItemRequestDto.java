package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.common.Constants.defaultJsonDateTimePattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String description;

    @JsonFormat(pattern = defaultJsonDateTimePattern)
    private LocalDateTime created;
    private List<ItemRequestReplyDto> items;
}
