package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.core.mapper.PaginationMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @PostMapping
    public ItemDto add(@RequestHeader(name = USER_ID_HEADER) long userId, @RequestBody ItemDto itemDto) {
        log.info("Запрос на создание предмета");
        return itemMapper.itemToItemDto(itemService.add(userId, itemDto.getRequestId(), itemMapper.itemDtoToItem(itemDto)));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable long itemId,
                          @RequestHeader(name = USER_ID_HEADER) long userId,
                          @RequestBody UpdateItemDto itemDto) {
        log.info("Запрос на обновление предмета");
        return itemMapper.itemToItemDto(itemService.update(itemId, userId, itemMapper.updateItemDtoToItem(itemDto)));
    }

    @DeleteMapping("/{itemId}")
    public void remove(@PathVariable long itemId) {
        log.info("Запрос на удаление предмета");
        itemService.remove(itemId);
    }

    @GetMapping("/{itemId}")
    public ItemGetDto getByItemId(@PathVariable long itemId, @RequestHeader(name = USER_ID_HEADER) long userId) {
        log.info("Запрос на выдачу предмета");
        return itemMapper.itemToItemGetDto(itemService.getByItemId(itemId, userId));
    }

    @GetMapping
    public List<ItemGetDto> getByOwnerId(@RequestHeader(name = USER_ID_HEADER) long ownerId,
                                         @RequestParam(required = false) Integer from,
                                         @RequestParam(required = false) Integer size) {
        log.info("Запрос на выдачу списка предметов владельца");
        return itemMapper.itemListToItemGetDtoList(itemService.getByOwnerId(ownerId, PaginationMapper.toMakePage(from, size)));
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(required = false) String text,
                                @RequestParam(required = false) Integer from,
                                @RequestParam(required = false) Integer size) {
        log.info("Запрос на поиск предметов по описанию и имени");
        return itemMapper.itemListToItemDtoList(itemService.search(text, PaginationMapper.toMakePage(from, size)));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto comment(@PathVariable long itemId,
                              @RequestHeader(name = USER_ID_HEADER) long userId,
                              @RequestBody AddCommentDto addCommentDto) {
        log.info("Запрос на создание коммента для предмета");
        return commentMapper.commentToCommentDto(
                itemService.addComment(itemId, userId, commentMapper.addCommentDtoToComment(addCommentDto)));
    }
}
