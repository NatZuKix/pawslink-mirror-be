package sit.pawslink.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secretKey;
    private Integer AccessTokenValidityInMinutes;
    private Integer RefreshTokenValidityInMinutes;
}
