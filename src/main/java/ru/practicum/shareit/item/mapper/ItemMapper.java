package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item itemDtoToItem(ItemDto itemDto);

    @Mapping(target = "requestId", source = "request.id")
    ItemDto itemToItemDto(Item item);

    Item updateItemDtoToItem(UpdateItemDto updateItemDto);

    ItemForBookingDto itemToItemForBookingDto(Item item);

    List<ItemDto> itemListToItemDtoList(List<Item> itemList);

    ItemGetDto itemToItemGetDto(Item item);

    List<ItemGetDto> itemListToItemGetDtoList(List<Item> itemList);
}
