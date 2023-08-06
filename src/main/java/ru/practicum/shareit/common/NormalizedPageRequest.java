package ru.practicum.shareit.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class NormalizedPageRequest extends PageRequest {
    public NormalizedPageRequest(int page, int size) {
        super(page > 0 ? page / size : 0, size, Sort.unsorted());
    }
}
