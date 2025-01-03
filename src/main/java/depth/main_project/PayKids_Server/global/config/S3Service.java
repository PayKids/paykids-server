package depth.main_project.PayKids_Server.global.config;

import depth.main_project.PayKids_Server.global.exception.ErrorCode;
import depth.main_project.PayKids_Server.global.exception.MapperException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    public String uploadToProfileImageFolder(String email, MultipartFile file) {
        String safeEmail = email.replaceAll("[^a-zA-Z0-9]", "_");
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss"));


        String uniqueFileName = "profile-image/" + safeEmail + "_" + currentTime;


        try {
            Path tempFile = Files.createTempFile("temp-", uniqueFileName);
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(uniqueFileName)
                            .contentType(file.getContentType())
                            .build(),
                    tempFile
            );

            return generateFileUrl(uniqueFileName);

        } catch (Exception e) {
            throw new MapperException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private String generateFileUrl(String uniqueFileName) {
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + uniqueFileName;
    }

    public void deleteFileFromS3(String fileUrl) {
        try {
            String key = fileUrl.substring(fileUrl.indexOf(".com/") + 5); // 파일 경로 추출
            s3Client.deleteObject(builder -> builder.bucket(bucketName).key(key).build());
        } catch (Exception e) {
            throw new MapperException(ErrorCode.FILE_DELETE_FAILED);
        }
    }
}
