package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemController(ItemService itemService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(HEADER_USER_ID) long userId, @Valid @RequestBody ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        log.info("В ItemController получен Post запрос addItem");
        return itemMapper.toItemDto(itemService.addItem(userId, item));
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER_USER_ID) long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        log.info("В ItemController получен PatchMapping запрос updateItem");
        return itemMapper.toItemDto(itemService.updateItem(userId, itemId, item));
    }

    @GetMapping(value = "/{itemId}")
    public ItemDto getItem(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable Long itemId) {
        log.info("В ItemController получен GetMapping запрос getItem");
        return itemMapper.toItemDto(itemService.getItem(userId, itemId));
    }

    @GetMapping
    public List<ItemDto> getAllItem(@RequestHeader(HEADER_USER_ID) long userId) {
        log.info("В ItemController получен GetMapping запрос getAllItem");
        return itemService.getAllItemsByIdOwner(userId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/search")
    public List<ItemDto> searchItem(@RequestHeader(HEADER_USER_ID) long userId,
                                    @RequestParam String text) {
        log.info("В ItemController получен GetMapping запрос searchItem");
        return itemService.searchItem(userId, text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}