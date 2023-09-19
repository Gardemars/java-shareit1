package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.core.exception.EntityNotFoundException;
import ru.practicum.shareit.core.exception.controller.ErrorHandler;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private MockMvc mockMvc;

    private User user;
    private UserDto userDto;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        user = new User(
                1L,
                "John",
                "john.doe@mail.com");

        userDto = new UserDto(
                1L,
                "John",
                "john.doe@mail.com");
    }

    @Test
    void addUser() throws Exception {
        RequestUserDto requestUserDto = new RequestUserDto(user.getName(), user.getEmail());

        when(userMapper.requestUserDtoToUser(requestUserDto)).thenReturn(user);
        when(userService.add(any())).thenReturn(user);
        when(userMapper.userToUserDto(userService.add(userMapper.requestUserDtoToUser(requestUserDto)))).thenReturn(userDto);


        verify(userService, times(1)).add(any());
        verify(userMapper, times(1)).requestUserDtoToUser(any());

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(requestUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
    }

    @Test
    void removeShouldReturnOkStatus() throws Exception {
        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    void getAllShouldReturnListOfUsers() throws Exception {
        List<UserDto> usersDto = List.of(
                userDto
        );

        List<User> users = List.of(
                user
        );

        when(userService.getAll()).thenReturn(users);
        when(userMapper.userListToUserDtoList(userService.getAll())).thenReturn(usersDto);

        verify(userService, times(1)).getAll();

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(usersDto)));
    }

    @Test
    void getAllShouldReturnEmptyList() throws Exception {
        List<UserDto> usersDto = Collections.emptyList();
        List<User> users = Collections.emptyList();

        when(userService.getAll()).thenReturn(users);
        when(userMapper.userListToUserDtoList(userService.getAll())).thenReturn(usersDto);

        verify(userService, times(1)).getAll();

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(usersDto)));
    }

    @Test
    void getByIdShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/users/why"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getByIdShouldReturnNotFound() throws Exception {

        when(userMapper.userToUserDto(any()))
                .thenThrow(new EntityNotFoundException((String.format("Пользователь с id = %d не найден в базе", userId))));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByIdShouldReturnUser() throws Exception {

        when(userService.getByUserId(userId)).thenReturn(user);
        when(userMapper.userToUserDto(userService.getByUserId(userId))).thenReturn(userDto);

        verify(userService, times(1)).getByUserId(userId);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
    }

    @Test
    void updateShouldUpdateUser() throws Exception {

        UpdateUserDto updateUserDto = new UpdateUserDto(user.getName(), user.getEmail());

        when(userMapper.updateUserDtoToUser(updateUserDto)).thenReturn(user);
        when(userService.update(any(), any())).thenReturn(user);
        when(userMapper.userToUserDto(userService.update(userId, userMapper.updateUserDtoToUser(updateUserDto))))
                .thenReturn(userDto);

        verify(userService, times(1)).update(any(), any());
        verify(userMapper, times(1)).updateUserDtoToUser(updateUserDto);

        mockMvc.perform(patch("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
    }

    @Test
    void updateShouldReturnNotFound() throws Exception {

        UpdateUserDto updateUserDto = new UpdateUserDto(user.getName(), user.getEmail());

        when(userMapper.userToUserDto(any()))
                .thenThrow(new EntityNotFoundException((String.format("Пользователь с id = %d не найден в базе", userId))));

        mockMvc.perform(patch("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isNotFound());
    }
}
