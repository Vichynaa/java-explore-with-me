package ru.practicum.explore.user.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.explore.user.dto.UserDto;
import ru.practicum.explore.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}
