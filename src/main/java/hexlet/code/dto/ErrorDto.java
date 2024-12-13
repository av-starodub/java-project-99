package hexlet.code.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ErrorDto {

    private final String error;

    private final List<String> details;

}
