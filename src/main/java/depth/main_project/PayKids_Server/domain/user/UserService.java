package depth.main_project.PayKids_Server.domain.user;

import depth.main_project.PayKids_Server.domain.user.dto.UserDTO;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import depth.main_project.PayKids_Server.domain.user.repository.UserRepository;
import depth.main_project.PayKids_Server.global.exception.ErrorCode;
import depth.main_project.PayKids_Server.global.exception.MapperException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDTO getUserInfo(String uuid) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        return new UserDTO(user.getId(), user.getUsername(), user.getUuid(), user.getNickname(), user.getEmail(), user.getProfileImageURL(), user.getStageStatus());
    }

    public String saveNickname(String uuid, String nickname) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        if (user.getUsername() != null) {
            throw new MapperException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        user.setUsername(nickname);
        userRepository.save(user);

        return "Nickname saved successfully";
    }

    public String changeNickname(String uuid, String newNickname) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));


        if (user.getUsername().equals(newNickname)) {
            throw new MapperException(ErrorCode.SAME_NICKNAME);
        }

        user.setUsername(newNickname); // 닉네임 변경
        userRepository.save(user);

        return "Nickname changed successfully";
    }

    public String changeEmail(String uuid, String newEmail) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        if (userRepository.existsByEmail(newEmail)) {
            throw new MapperException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (user.getEmail().equals(newEmail)) {
            throw new MapperException(ErrorCode.SAME_EMAIL);
        }

        user.setEmail(newEmail);
        userRepository.save(user);
        return "Email changed successfully";
    }
}
