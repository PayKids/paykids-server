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

    public UserDTO getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        return new UserDTO(user.getId(), user.getUsername(), user.getNickname(), user.getEmail(), user.getProfileImageURL(), user.getStageStatus());
    }
}
