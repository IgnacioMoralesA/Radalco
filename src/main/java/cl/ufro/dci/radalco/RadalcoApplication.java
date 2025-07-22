package cl.ufro.dci.radalco;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RadalcoApplication {

    public static void main(String[] args) {
        // Cargar variables del .env
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        String dbHost = dotenv.get("DB_HOST");
        if (dbHost != null) System.setProperty("DB_HOST", dbHost);

        String dbPort = dotenv.get("DB_PORT");
        if (dbPort != null) System.setProperty("DB_PORT", dbPort);

        String dbName = dotenv.get("DB_NAME");
        if (dbName != null) System.setProperty("DB_NAME", dbName);

        String dbUser = dotenv.get("DB_USER");
        if (dbUser != null) System.setProperty("DB_USER", dbUser);

        String dbPassword = dotenv.get("DB_PASSWORD");
        if (dbPassword != null) System.setProperty("DB_PASSWORD", dbPassword);


        SpringApplication.run(RadalcoApplication.class, args);
    }

}
