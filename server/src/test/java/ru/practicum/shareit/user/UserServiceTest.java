package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.core.exception.DuplicateEmailException;
import ru.practicum.shareit.core.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;
    private final Long userId = 1L;

    private User user = new User(
            userId,
            "John",
            "john.doe@mail.com");


    @Test
    void addShouldReturnUser() {
        when(userRepository.save(user)).thenReturn(user);
        assertThat(userService.add(user)).isEqualTo(user);
    }

    @Test
    void addShouldReturnDuplicateEmailException() {
        when(userRepository.save(user)).thenThrow(new DataIntegrityViolationException(""));
        assertThatThrownBy(() -> userService.add(user)).isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void updateShouldThrowEntityNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getByUserId(userId)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateShouldUpdateEmail() {
        User newUser = User.builder()
                .id(null)
                .name(null)
                .email("newEmail@test.test").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        assertThat(userService.update(userId, newUser)).isEqualTo(user);
    }

    @Test
    void updateShouldUpdateName() {
        User newUser = User.builder()
                .id(null)
                .name("newName")
                .email(null).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        assertThat(userService.update(userId, newUser)).isEqualTo(user);
    }

    @Test
    void updateShouldThrowDuplicateEmailException() {
        User newUser = User.builder()
                .id(null)
                .name("newName")
                .email(null).build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenThrow(new DataIntegrityViolationException(""));

        assertThatThrownBy(() -> userService.update(userId, newUser)).isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void getAllShouldReturnListOfUsers() {
        List<User> users = List.of(user);

        when(userRepository.findAll()).thenReturn(users);

        assertThat(userService.getAll()).isEqualTo(users);
    }

    @Test
    void getByUserIdShouldThrowEntityNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getByUserId(userId)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getByIdShouldReturnUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertThat(userService.getByUserId(userId)).isEqualTo(user);
    }
}
