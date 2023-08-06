package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT r FROM ItemRequest r LEFT JOIN FETCH r.responses WHERE r.requesterId = :userId ORDER BY r.created DESC")
    List<ItemRequest> getByRequesterIdWithResponses(@Param("userId") Long userId);

    @Query("SELECT r FROM ItemRequest r LEFT JOIN FETCH r.responses")
    List<ItemRequest> getAllWithResponses(Pageable page);

    @Query("SELECT r FROM ItemRequest r LEFT JOIN FETCH r.responses WHERE r.id = :id")
    Optional<ItemRequest> getByIdWithResponses(@Param("id") Long id);

}