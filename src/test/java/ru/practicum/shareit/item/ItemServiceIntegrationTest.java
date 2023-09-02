package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;

    @Test
    void shouldCreateItemsAndGetByUserId() {
        User user = new User(
                null,
                "John",
                "john.doe@mail.com");

        User user1 = new User(
                null,
                "Jo",
                "jo.do@mail.com");

        Item item = new Item(
                null,
                "name",
                "description",
                true,
                null,
                null,
                null,
                null,
                null);

        Item item1 = new Item(
                null,
                "name-1",
                "description-1",
                true,
                null,
                null,
                null,
                null,
                null);

        user = userService.add(user);
        user1 = userService.add(user1);

        itemService.add(user.getId(), null, item);
        itemService.add(user1.getId(), null, item1);


        List<Item> items = itemService.getByOwnerId(user.getId(), null);

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getId()).isEqualTo(1L);
        assertThat(items.get(0).getName()).isEqualTo(item.getName());

    }
}
