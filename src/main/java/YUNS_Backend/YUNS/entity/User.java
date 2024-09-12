package YUNS_Backend.YUNS.entity;

import YUNS_Backend.YUNS.dto.UserRegisterDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(name = "student_number", nullable = false, unique = true)
    private String studentNumber;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean userRentalStatus;

    public static User createUser(UserRegisterDto userRegisterDto, PasswordEncoder passwordEncoder){
        String password = passwordEncoder.encode(userRegisterDto.getPassword());

        User user = User.builder()
                .studentNumber(userRegisterDto.getStudentNumber())
                .name(userRegisterDto.getName())
                .password(password)
                .phoneNumber(userRegisterDto.getPhoneNumber())
                .email(userRegisterDto.getEmail())
                .role(Role.USER)
                .userRentalStatus(false)
                .build();

        return user;
    }
}
