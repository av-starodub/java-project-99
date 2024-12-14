package hexlet.code.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public final class JwtUtils {

    private final JwtEncoder encoder;

    public String generateToken(String subject) {
        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(subject)
                .build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}
