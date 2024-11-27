package depth.main_project.PayKids_Server.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MapperException extends RuntimeException{
    private final ErrorCode errorCode;
}
