package complaint_management.service;

import complaint_management.dto.LoginRequestDTO;
import complaint_management.dto.LoginResponseDTO;
import complaint_management.dto.RegisterRequestDTO;
import complaint_management.dto.UserResponseDTO;
import complaint_management.entity.User;
import complaint_management.enums.Role;
import complaint_management.repository.UserRepository;
import complaint_management.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // ---------------- REGISTER ----------------

    public UserResponseDTO register(RegisterRequestDTO requestDTO) {

        User user = new User();

        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        UserResponseDTO responseDTO = new UserResponseDTO();

        responseDTO.setId(savedUser.getId());
        responseDTO.setName(savedUser.getName());
        responseDTO.setEmail(savedUser.getEmail());
        responseDTO.setRole(savedUser.getRole());

        return responseDTO;
    }

    // ---------------- LOGIN ----------------

    public LoginResponseDTO login(LoginRequestDTO requestDTO) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getEmail(),
                        requestDTO.getPassword()
                )
        );

        String token = jwtService.generateToken(requestDTO.getEmail());

        return new LoginResponseDTO(token);
    }
}