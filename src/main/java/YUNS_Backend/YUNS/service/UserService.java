package YUNS_Backend.YUNS.service;

import YUNS_Backend.YUNS.custom.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import YUNS_Backend.YUNS.entity.User;
import YUNS_Backend.YUNS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new IllegalStateException("이미 가입된 회원입니다.");
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
}
