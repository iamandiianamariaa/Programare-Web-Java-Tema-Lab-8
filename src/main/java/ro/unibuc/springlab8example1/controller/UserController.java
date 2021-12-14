package ro.unibuc.springlab8example1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.springlab8example1.domain.UserType;
import ro.unibuc.springlab8example1.dto.UserDto;
import ro.unibuc.springlab8example1.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/student")
    public ResponseEntity<UserDto> createStudent(@RequestBody UserDto userDto) {
        return ResponseEntity
                .ok()
                .body(userService.create(userDto, UserType.STUDENT));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> get(@PathVariable String username) {
        return ResponseEntity
                .ok()
                .body(userService.getOne(username));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserDto> updateStudent(@RequestBody UserDto userDto, @PathVariable Long id) {
        return ResponseEntity
                .ok()
                .body(userService.updateUserWithId(userDto, id, UserType.STUDENT));
    }

    @GetMapping("/userByType/{userType}")
    public ResponseEntity<List<UserDto>> get(@PathVariable UserType userType) {
        return ResponseEntity
                .ok()
                .body(userService.getUsersByType(userType));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<UserDto> delete(@PathVariable Long id) {
        userService.deleteUserWithId(id);
        return ResponseEntity.noContent().build();
    }

    // TODO: homework: endpoints for updating a user, deleting one, get all users filtered by tupe
}
