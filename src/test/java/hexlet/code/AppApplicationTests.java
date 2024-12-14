package hexlet.code;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class AppApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("Should initialize admin data")
    void checkAdminDataInitialization() {
        var admin = userRepository.findByEmail("hexlet@example.com").orElse(null);
        assertThat(admin)
                .isNotNull()
                .isInstanceOf(User.class);
    }

}
