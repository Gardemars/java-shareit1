package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemMapperTest {
    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    private Long userId;
    private Long itemId;
    private Long requestId;

    private User user;
    private Item item;
    private Request request;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        userId = 1L;
        itemId = 1L;
        requestId = 1L;

        user = new User(
                userId,
                "John",
                "john.doe@mail.com");

        item = new Item(
                itemId,
                "name",
                "description",
                true,
                user,
                new BookingShortDto(2L, 2L),
                new BookingShortDto(3L, 2L),
                null,
                null);

        request = new Request(
                requestId,
                "description",
                LocalDateTime.now(),
                user);

        itemDto = new ItemDto(
                itemId,
                "item",
                "description",
                true,
                null);
    }

    @Test
    void itemToItemGetDto() {
        ItemGetDto dto = itemMapper.itemToItemGetDto(item);
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
    }

    @Test
    void itemToItemDtoWithoutRequest() {
        ItemDto dto = itemMapper.itemToItemDto(item);
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
    }

    @Test
    void itemToItemDto() {
        item.setRequest(request);
        ItemDto dto = itemMapper.itemToItemDto(item);
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
        assertThat(dto.getRequestId()).isEqualTo(request.getId());
    }

    @Test
    void itemDtoToItem() {
        Item item1 = itemMapper.itemDtoToItem(itemDto);
        assertThat(item1.getId()).isEqualTo(itemDto.getId());
        assertThat(item1.getName()).isEqualTo(itemDto.getName());
    }

    @Test
    void itemListToItemDtoList() {
        List<ItemDto> list = itemMapper.itemListToItemDtoList(List.of(item));

        assertThat(list.get(0).getId()).isEqualTo(item.getId());
    }

    @Test
    void itemListToItemGetDtoList() {
        List<ItemGetDto> list = itemMapper.itemListToItemGetDtoList(List.of(item));
        assertThat(list.get(0).getId()).isEqualTo(item.getId());
    }

    @Test
    void itemToItemForBookingDto() {
        ItemForBookingDto dto = itemMapper.itemToItemForBookingDto(item);
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
    }

    @Test
    void updateItemDtoToItem() {
        UpdateItemDto updateItemDto = new UpdateItemDto(2L, "name", "text", true);
        Item item1 = itemMapper.updateItemDtoToItem(updateItemDto);
        assertThat(updateItemDto.getId()).isEqualTo(item1.getId());
        assertThat(updateItemDto.getName()).isEqualTo(item1.getName());
    }

}
