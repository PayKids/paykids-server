package depth.main_project.PayKids_Server.domain.quiz.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import depth.main_project.PayKids_Server.domain.quiz.dto.QuizDTO;
import depth.main_project.PayKids_Server.domain.quiz.entity.*;
import depth.main_project.PayKids_Server.domain.quiz.repository.QuizRepository;
import depth.main_project.PayKids_Server.domain.quiz.repository.StageNameRepository;
import depth.main_project.PayKids_Server.domain.quiz.repository.SubmissionRepository;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import depth.main_project.PayKids_Server.domain.user.repository.UserRepository;
import depth.main_project.PayKids_Server.global.exception.ErrorCode;
import depth.main_project.PayKids_Server.global.exception.MapperException;
import depth.main_project.PayKids_Server.domain.auth.TokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final StageNameRepository stageNameRepository;
    private final SubmissionRepository submissionRepository;
    private final TokenService tokenService;

    //스테이지 이름 반환 메서드
    public String getStageName(int number){
        StageName stageName = stageNameRepository.findOneByStageNumber(number);

        if (stageName == null) {
            throw new MapperException(ErrorCode.STAGE_NOT_FOUND);
        }

        return stageName.getStageName();
    }

    //현재 진행해야하는 스테이지 번호 반환 메서드
    public int getUserStageStatus(String token){
        String userUUID = tokenService.getUserUuidFromToken(token);

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userUUID == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userUUID)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        return user.getStageStatus();
    }


    //퀴즈 반환 메서드
    public QuizDTO getQuiz(int stage, int number){
        Quiz quiz = quizRepository.findByNumberAndStage(number, stage)
                .orElseThrow(() -> new MapperException(ErrorCode.QUIZ_NOT_FOUND));

        ObjectMapper objectMapper = new ObjectMapper();

        return QuizDTO.fromEntity(quiz, objectMapper);
    }

    //정답 확인 메서드, 제출된 답안 저장까지 진행
    @Transactional
    public Boolean isQuizAnswerTrue(int stage, int number, String answer, String token){
        String userUUID = tokenService.getUserUuidFromToken(token);

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userUUID == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        Quiz quiz = quizRepository.findByNumberAndStage(number, stage)
                .orElseThrow(() -> new MapperException(ErrorCode.QUIZ_NOT_FOUND));

        User user = userRepository.findByUuid(userUUID)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        if (user.getStageStatus() > stage){
            String realAnswer = quiz.getAnswer();

            if (realAnswer.equals(answer)){
                return true;
            }

            return false;
        }

        if (quiz.getQuizType() == QuizType.SHORT_ANSWER){
            answer = answer.replaceAll("\\s", "");
            String realAnswer = quiz.getAnswer().replaceAll("\\s", "");

            if (realAnswer.equals(answer)){
                if (submissionRepository.findByUserAndQuiz(user, quiz).isPresent()){
                    Submission existSubmission = submissionRepository.findByUserAndQuiz(user, quiz)
                            .orElseThrow();

                    existSubmission.setStatus(Status.RESUBMITTED);
                    existSubmission.setIsAnswerTrue(true);
                    existSubmission.setSubmitDateTime(LocalDateTime.now());

                    submissionRepository.save(existSubmission);
                    return true;
                }

                Submission submission = Submission.builder()
                        .quiz(quiz)
                        .user(user)
                        .status(Status.CORRECT)
                        .isAnswerTrue(true)
                        .submitDateTime(LocalDateTime.now())
                        .build();

                submissionRepository.save(submission);
                return true;
            } else {
                if (submissionRepository.findByUserAndQuiz(user, quiz).isPresent()) {
                    Submission existSubmission = submissionRepository.findByUserAndQuiz(user, quiz)
                            .orElseThrow();

                    existSubmission.setStatus(Status.RESUBMITTED);
                    existSubmission.setSubmitDateTime(LocalDateTime.now());

                    submissionRepository.save(existSubmission);
                    return false;
                }
            }
        } else {
            String realAnswer = quiz.getAnswer();

            if (realAnswer.equals(answer)){
                if (submissionRepository.findByUserAndQuiz(user, quiz).isPresent()){
                    Submission existSubmission = submissionRepository.findByUserAndQuiz(user, quiz)
                            .orElseThrow();

                    existSubmission.setStatus(Status.RESUBMITTED);
                    existSubmission.setIsAnswerTrue(true);
                    existSubmission.setSubmitDateTime(LocalDateTime.now());

                    submissionRepository.save(existSubmission);
                    return true;
                }

                Submission submission = Submission.builder()
                        .quiz(quiz)
                        .user(user)
                        .status(Status.CORRECT)
                        .isAnswerTrue(true)
                        .submitDateTime(LocalDateTime.now())
                        .build();

                submissionRepository.save(submission);
                return true;
            } else {
                if (submissionRepository.findByUserAndQuiz(user, quiz).isPresent()) {
                    Submission existSubmission = submissionRepository.findByUserAndQuiz(user, quiz)
                            .orElseThrow();

                    existSubmission.setStatus(Status.RESUBMITTED);
                    existSubmission.setSubmitDateTime(LocalDateTime.now());

                    submissionRepository.save(existSubmission);
                    return false;
                }
            }
        }

        Submission submission = Submission.builder()
                .quiz(quiz)
                .user(user)
                .status(Status.INCORRECT)
                .isAnswerTrue(false)
                .submitDateTime(LocalDateTime.now())
                .build();

        submissionRepository.save(submission);

        return false;
    }

    @Transactional
    public Boolean isPassedStage(int stage, String token){
        String userUUID = tokenService.getUserUuidFromToken(token);

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userUUID == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userUUID)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<Submission> checkStatus = submissionRepository.findAllByUser(user);

        if (checkStatus == null || checkStatus.isEmpty()){
            throw new MapperException(ErrorCode.STAGE_NOT_FOUND);
        }

        int count = 0;
        int realCount = 100;

        for (Submission submission : checkStatus){
            if (submission.getIsAnswerTrue() && submissionRepository.findQuizStageBySubmissionId(submission.getId()) == stage){
                count++;
                realCount = submissionRepository.findQuizCountBySubmissionId(submission.getId());
            }
        }

        if (realCount / 2 <= count && user.getStageStatus() == stage){
            user.setStageStatus(stage + 1);
            userRepository.save(user);

            return true;
        }

        return false;
    }

    @Transactional
    public Boolean isAllCleared(int stage, String token){
        String userUUID = tokenService.getUserUuidFromToken(token);

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userUUID == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userUUID)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<Submission> checkStatus = submissionRepository.findAllByUser(user);

        if (checkStatus == null || checkStatus.isEmpty()){
            throw new MapperException(ErrorCode.STAGE_NOT_FOUND);
        }

        int count = 0;
        int realCount = 100;

        for (Submission submission : checkStatus){
            if (submission.getIsAnswerTrue() && submissionRepository.findQuizStageBySubmissionId(submission.getId()) == stage){
                count++;
                realCount = submissionRepository.findQuizCountBySubmissionId(submission.getId());
            }
        }

        if (realCount == count && user.getStageStatus() == stage){
            user.setStageStatus(stage + 1);
            userRepository.save(user);

            return true;
        }

        return false;
    }

    //복습인지 아닌지 확인
    public Boolean isRestudy(int stage, String token){
        String userUUID = tokenService.getUserUuidFromToken(token);
        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userUUID == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userUUID)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        if (user.getStageStatus() > stage){
            return true;
        }

        return false;
    }

    //오답 리스트 제공
    public List<Integer> getIncorrectQuizList(int stage, String token){
        String userUUID = tokenService.getUserUuidFromToken(token);
        List<Integer> quizList = new ArrayList<>();

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userUUID == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userUUID)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<Submission> checkStatus = submissionRepository.findAllByUser(user);

        if (checkStatus == null || checkStatus.isEmpty()){
            throw new MapperException(ErrorCode.STAGE_NOT_FOUND);
        }

        for (Submission submission : checkStatus){
            if (submission.getIsAnswerTrue() == false && submissionRepository.findQuizStageBySubmissionId(submission.getId()) == stage){
                quizList.add(submissionRepository.findQuizNumberBySubmissionId(submission.getId()));
            }
        }

        return quizList;
    }

    //스테이지 도전 기록이 있는지 확인
    public Boolean isNotYet(int stage, String token){
        String userUUID = tokenService.getUserUuidFromToken(token);

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userUUID == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userUUID)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<Submission> checkStatus = submissionRepository.findAllByUser(user);

        if (checkStatus == null || checkStatus.isEmpty()){
            return true;
        }

        int count = 0;
        int realCount = 0;

        for (Submission submission : checkStatus){
            if(submissionRepository.findQuizStageBySubmissionId(submission.getId()) == stage){
                count++;
                realCount = submissionRepository.findQuizCountBySubmissionId(submission.getId());
            }
        }

        if (realCount > count) {
            return true;
        }

        return false;
    }

    public Boolean isIncorrect (int stage, String token){
        String userUUID = tokenService.getUserUuidFromToken(token);

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userUUID == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userUUID)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<Submission> checkStatus = submissionRepository.findAllByUser(user);

        if (checkStatus == null || checkStatus.isEmpty()){
            return true;
        }

        for (Submission submission : checkStatus){
            if (submission.getStatus() == Status.RESUBMITTED && submissionRepository.findQuizStageBySubmissionId(submission.getId()) == stage){
                return true;
            }
        }

        return false;
    }

    public int getTotalQuizCount(){
        Long longCount = stageNameRepository.count();
        int intCount = longCount.intValue();

        return intCount;
    }
}
