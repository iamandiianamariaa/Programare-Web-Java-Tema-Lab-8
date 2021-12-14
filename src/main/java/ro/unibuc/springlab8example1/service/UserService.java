package ro.unibuc.springlab8example1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.unibuc.springlab8example1.domain.User;
import ro.unibuc.springlab8example1.domain.UserType;
import ro.unibuc.springlab8example1.dto.UserDto;
import ro.unibuc.springlab8example1.mapper.UserMapper;
import ro.unibuc.springlab8example1.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public UserDto create(UserDto userDto, UserType type) {
        User user = userMapper.mapToEntity(userDto);
        user.setUserType(type);
        User savedUser = userRepository.save(user);

        return userMapper.mapToDto(savedUser);
    }

    public UserDto getOne(String username) {
        return userMapper.mapToDto(userRepository.get(username));
    }

    public UserDto updateUserWithId(UserDto userDto, Long id, UserType type) {
        User user = userMapper.mapToEntity(userDto);
        user.setUserType(type);
        return userMapper.mapToDto(userRepository.updateUserWithId(user, id));
    }

    public void deleteUserWithId(Long id) {
        userRepository.deleteUserWithId(id);
    }

    public List<UserDto> getUsersByType(UserType userType) {
        List<User> users = userRepository.getUsersByType(userType);
        List<UserDto> userDtoList = new ArrayList<>();
        for(User user : users){
            UserDto userDto = new UserDto();
            userDto = userMapper.mapToDto(user);
            userDtoList.add(userDto);
        }
        return userDtoList;
    }
}
