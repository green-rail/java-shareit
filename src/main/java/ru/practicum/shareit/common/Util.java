package ru.practicum.shareit.common;

import ru.practicum.shareit.error.exception.InvalidEntityException;

public class Util {
    public static void checkPageRequestBoundaries(int from, int size) {
        if (from < 0 || size < 0) {
            throw new InvalidEntityException("начальный индекс и размер выборки должны быть неотрицательны");
        }
    }
}
