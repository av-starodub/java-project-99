package hexlet.code.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum DefaultTaskStatusType {
    NEW("New", "new"),
    PROGRESS("Progress", "progress"),
    REVIEW("Review", "review"),
    DONE("Done", "done");

    private final String name;

    private final String slug;

    public static List<DefaultTaskStatusType> getAll() {
        return Arrays.asList(DefaultTaskStatusType.values());
    }

    public static List<String> getAllDefaultStatusNames() {
        return getAll().stream()
                .map(DefaultTaskStatusType::getName)
                .toList();
    }
}
