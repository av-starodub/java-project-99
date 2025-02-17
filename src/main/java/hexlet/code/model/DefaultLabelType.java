package hexlet.code.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum DefaultLabelType {
    FEATURE("feature"),
    BUG("bug");

    private final String name;

    public static List<DefaultLabelType> getAll() {
        return Arrays.asList(DefaultLabelType.values());
    }

    public static List<String> getAllDefaultLabelNames() {
        return getAll().stream()
                .map(DefaultLabelType::getName)
                .toList();
    }
}
