package complaint_management.controller;

import complaint_management.dto.LoginRequestDTO;
import complaint_management.dto.LoginResponseDTO;
import complaint_management.dto.RegisterRequestDTO;
import complaint_management.dto.UserResponseDTO;
import complaint_management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // ---------------- REGISTER ----------------

    @PostMapping("/register")
    public UserResponseDTO register(
            @Valid @RequestBody RegisterRequestDTO requestDTO) {

        return userService.register(requestDTO);
    }

    // ---------------- LOGIN ----------------

    @PostMapping("/login")
    public LoginResponseDTO login(
            @Valid @RequestBody LoginRequestDTO requestDTO) {

        return userService.login(requestDTO);
    }
}