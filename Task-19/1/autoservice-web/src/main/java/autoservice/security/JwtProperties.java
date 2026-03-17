package autoservice.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtProperties {
    private final String secret;
    private final long expiration;
    private final String header;
    private final String prefix;

    public JwtProperties(
            @Value("${jwt.secret:mySecretKeyForJwtTokenGenerationAndValidation}") String secret,
            @Value("${jwt.expiration:864000000}") long expiration,
            @Value("${jwt.header:Authorization}") String header,
            @Value("${jwt.prefix:Bearer }") String prefix
    ) {
        this.secret = secret;
        this.expiration = expiration;
        this.header = header;
        this.prefix = prefix;
    }
}