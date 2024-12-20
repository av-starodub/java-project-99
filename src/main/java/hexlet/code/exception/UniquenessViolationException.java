package hexlet.code.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class UniquenessViolationException extends RuntimeException {

    private final List<String> details;

}
