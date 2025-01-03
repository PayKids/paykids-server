package depth.main_project.PayKids_Server.domain.user;

import depth.main_project.PayKids_Server.domain.user.dto.UserDTO;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import depth.main_project.PayKids_Server.domain.user.repository.UserRepository;
import depth.main_project.PayKids_Server.global.config.S3Service;
import depth.main_project.PayKids_Server.global.exception.ErrorCode;
import depth.main_project.PayKids_Server.global.exception.MapperException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;

    public UserDTO getUserInfo(String uuid) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        return new UserDTO(user.getId(), user.getUsername(), user.getUuid(), user.getNickname(), user.getEmail(), user.getProfileImageURL(), user.getStageStatus());
    }

    public String saveNickname(String uuid, String nickname) {
        if (nickname.codePointCount(0, nickname.length()) > 9) {
            throw new MapperException(ErrorCode.NICKNAME_TOO_LONG); // 닉네임 길이 초과
        }

        if (nickname == null || nickname.trim().isEmpty()) {
            throw new MapperException(ErrorCode.INVALID_NICKNAME); // 닉네임이 공백인 경우 예외 처리
        }

        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        if (user.getNickname() != null) {
            throw new MapperException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        user.setNickname(nickname);
        userRepository.save(user);

        return "Nickname saved successfully";
    }

    public String changeNickname(String uuid, String newNickname) {
        if (newNickname.codePointCount(0, newNickname.length()) > 9) {
            throw new MapperException(ErrorCode.NICKNAME_TOO_LONG); // 닉네임 길이 초과
        }

        if (newNickname == null || newNickname.trim().isEmpty()) {
            throw new MapperException(ErrorCode.INVALID_NICKNAME);
        }

        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));


        if (user.getNickname().equals(newNickname)) {
            throw new MapperException(ErrorCode.SAME_NICKNAME);
        }

        user.setNickname(newNickname); // 닉네임 변경
        userRepository.save(user);

        return "Nickname changed successfully";
    }

    public String changeProfileImage(String uuid, MultipartFile file) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        // 이전 프로필 이미지 삭제
        String previousImageUrl = user.getProfileImageURL();
        deletePreviousProfileImage(previousImageUrl);

        // S3에 파일 업로드 및 URL 반환
        String email = user.getEmail();
        String newImageUrl = s3Service.uploadToProfileImageFolder(email, file);

        // 프로필 이미지 URL을 DB에 업데이트
        user.setProfileImageURL(newImageUrl);
        userRepository.save(user);

        return newImageUrl;
    }

    private void deletePreviousProfileImage(String previousImageUrl) {
        if (previousImageUrl == null) {
            throw new MapperException(ErrorCode.PREVIOUS_IMAGE_NOT_FOUND);
        }

        if (previousImageUrl.contains(".s3.") && previousImageUrl.contains(".amazonaws.com")) {
            s3Service.deleteFileFromS3(previousImageUrl); // S3에서 삭제
        }
    }
}
