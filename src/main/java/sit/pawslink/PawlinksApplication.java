package sit.pawslink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import sit.pawslink.properties.JwtProperties;
import sit.pawslink.repositories.CustomRepositoryImpl;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = CustomRepositoryImpl.class)
@EnableConfigurationProperties({JwtProperties.class})
public class PawlinksApplication {

    public static void main(String[] args) {
        SpringApplication.run(PawlinksApplication.class, args);
    }

}
