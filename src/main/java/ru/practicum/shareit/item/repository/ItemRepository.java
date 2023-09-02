package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long userId, Pageable pageable);

    List<Item> findAllByOwnerId(Long userId);

    List<Item> findAllByRequestId(Long requestId);

    @Query(value = "SELECT * FROM items i " +
            "WHERE LOWER(i.NAME) LIKE :text OR LOWER(i.DESCRIPTION) LIKE :text " +
            "AND i.AVAILABLE IS TRUE", nativeQuery = true)
    List<Item> findByText(@Param("text") String text, Pageable pageable);
}