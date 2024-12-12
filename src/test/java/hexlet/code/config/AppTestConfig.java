package hexlet.code.config;

import net.datafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppTestConfig {

    /**
     * faker.
     *
     * @return {@link Faker}
     * @see Faker
     */
    @Bean
    public Faker faker() {
        return new Faker();
    }
}
