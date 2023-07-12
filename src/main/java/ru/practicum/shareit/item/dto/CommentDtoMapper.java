package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Comment;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentDtoMapper {

    public static CommentDto toDto(Comment comment, String authorName) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                authorName,
                comment.getCreated());
    }
}
