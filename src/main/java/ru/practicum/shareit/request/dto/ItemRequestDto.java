package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String description;

    @JsonFormat(pattern = jsonDateTimePattern, timezone = "UTC")
    private Instant created;
    private List<ItemRequestReplyDto> items;

    public static final String jsonDateTimePattern = "yyyy-MM-dd'T'HH:mm:ss";
}
