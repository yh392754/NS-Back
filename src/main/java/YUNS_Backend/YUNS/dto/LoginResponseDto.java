package YUNS_Backend.YUNS.dto;

import YUNS_Backend.YUNS.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class LoginResponseDto {
    private Long userId;
    private String name;
    private String studentNumber;
    private String phoneNumber;
    private String email;
    private Role role;
    private boolean userRentalStatus;
    private String accessToken;

}
