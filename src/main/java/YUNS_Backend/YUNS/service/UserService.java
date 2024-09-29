package YUNS_Backend.YUNS.service;

import YUNS_Backend.YUNS.custom.CustomUserDetails;
import YUNS_Backend.YUNS.exception.CustomException;
import YUNS_Backend.YUNS.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import YUNS_Backend.YUNS.entity.User;
import YUNS_Backend.YUNS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public User registerUser(User user){
        validateDuplicateUser(user);
        return userRepository.save(user);
    }

    private void validateDuplicateUser(User user) {
        User findUser = userRepository.findByStudentNumber(user.getStudentNumber());
        if (findUser != null) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXIST);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String studentNumber) throws UsernameNotFoundException {
        log.info("studentNubmer? : "+studentNumber);

        User user = userRepository.findByStudentNumber(studentNumber);

        log.info("Is user null? : "+(user == null));

        if (user == null) {
            throw new UsernameNotFoundException(studentNumber);
        }

        return new CustomUserDetails(user);
    }

    public User findUserByStudentNumber(String studentNumber) {
        User user = userRepository.findByStudentNumber(studentNumber);
        if (user == null) {
            throw new UsernameNotFoundException(studentNumber);
        }
        return user;
    }

    public void deleteUser(Long userId){
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }
}
