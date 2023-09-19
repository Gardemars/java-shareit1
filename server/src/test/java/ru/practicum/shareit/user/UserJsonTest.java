package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    private UserDto userDto = new UserDto(
            1L,
            "John",
            "john.doe@mail.com");

    @Test
    public void serialization() throws Exception {
        String expected = "{\"id\": 1, \"name\": \"John\", \"email\": \"john.doe@mail.com\"}";

        JsonContent<UserDto> result = json.write(userDto);
        assertThat(result).isEqualToJson(expected);
    }

    @Test
    public void deserialization() throws Exception {
        String data = "{\"id\": 1, \"name\": \"John\", \"email\": \"john.doe@mail.com\"}";
        UserDto result = json.parse(data).getObject();

        assertThat(result.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(result.getName()).isEqualTo(userDto.getName());
        assertThat(result.getId()).isEqualTo(userDto.getId());
    }
}
