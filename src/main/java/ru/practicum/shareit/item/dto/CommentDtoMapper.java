package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Comment;

@UtilityClass
public class CommentDtoMapper {

    public CommentDto toDto(Comment comment, String authorName) {
        return new CommentDto(
                comment.getId(),
                comment.getCommentText(),
                authorName,
                comment.getCreated());
    }
}
