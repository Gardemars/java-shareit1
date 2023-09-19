package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        assertThat(entityManager).isNotNull();
    }

    @Test
    void searchShouldReturnListItem() {
        String text = "apPle";

        User user = User.builder()
                .id(1L)
                .name("Test name")
                .email("test@test.test").build();

        userRepository.save(user);

        Item item1 = new Item(1L, "Apple", "description", true, user, null, null, null, null);
        Item item2 = new Item(2L, "ap", "description", true, user, null, null, null, null);
        Item item3 = new Item(3L, "aPPle", "description", true, user, null, null, null, null);

        assertThat(itemRepository.findByText("%" + text.toLowerCase() + "%", null)).hasSize(0);

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        assertThat(itemRepository.findByText("%" + text.toLowerCase() + "%", null)).hasSize(2);
    }
}
