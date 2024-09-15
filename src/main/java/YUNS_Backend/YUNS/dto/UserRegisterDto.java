package YUNS_Backend.YUNS.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserRegisterDto {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "학번은 필수 입력 값입니다.")
    private String studentNumber;


    @NotBlank(message = "비밀번호는 필수 입력 값입니다")
    @Size(min = 10, message = "비밀번호는 최소 10자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "휴대폰 번호는 필수 입력 값입니다")
    private String phoneNumber;

    @NotBlank(message = "이메일은 필수 입력 값입니다")
    @Email(message = "이메일 형식으로 입력해주세요")
    private String email;
}
