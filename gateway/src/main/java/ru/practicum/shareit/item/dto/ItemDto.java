package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private String name;

    private String description;

    private Boolean available;
    private Long requestId;

    public boolean canCreate() {
        return name != null && !name.isBlank()
                && description != null && !description.isBlank()
                && available != null;
    }

    public boolean canUpdate() {
        boolean canUpdate = true;
        if (name != null) {
            if (name.isBlank())
                return false;
        }
        if (description != null) {
            return !description.isBlank();
        }
        return true;
    }
}
