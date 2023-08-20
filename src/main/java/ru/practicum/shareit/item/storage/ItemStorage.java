package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemStorage extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long userId);

    @Query(value = "SELECT * FROM ITEMS i " +
            "WHERE LOWER(i.NAME) LIKE ?1 OR LOWER(i.DESCRIPTION) LIKE ?1 " +
            "AND i.AVAILABLE IS TRUE", nativeQuery = true)
    List<Item> findByText(String text);
}
