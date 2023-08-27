package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @PostMapping
    public ItemDto add(@RequestHeader(name = USER_ID_HEADER) long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemMapper.itemToItemDto(itemService.add(userId, itemMapper.itemDtoToItem(itemDto)));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable long itemId,
                          @RequestHeader(name = USER_ID_HEADER) long userId,
                          @Valid @RequestBody UpdateItemDto itemDto) {
        return itemMapper.itemToItemDto(itemService.update(itemId, userId, itemMapper.updateItemDtoToItem(itemDto)));
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable long itemId) {
        itemService.remove(itemId);
    }

    @GetMapping("/{itemId}")
    public ItemGetDto getByItemId(@PathVariable long itemId, @RequestHeader(name = USER_ID_HEADER) long userId) {
        return itemMapper.itemToItemGetDto(itemService.getByItemId(itemId, userId));
    }

    @GetMapping
    public List<ItemGetDto> getByOwnerId(@RequestHeader(name = USER_ID_HEADER) long ownerId) {
        return itemMapper.itemListToItemGetDtoList(itemService.getByOwnerId(ownerId));
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(required = false) String text) {
        return itemMapper.itemListToItemDtoList(itemService.search(text));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto comment(
            @PathVariable long itemId,
            @RequestHeader(name = USER_ID_HEADER) long userId,
            @Valid @RequestBody AddCommentDto addCommentDto
    ) {
        return commentMapper.commentToCommentDto(
                itemService.addComment(itemId, userId, commentMapper.addCommentDtoToComment(addCommentDto)));
    }
}