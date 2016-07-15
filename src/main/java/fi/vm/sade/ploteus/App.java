package fi.vm.sade.ploteus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@Configuration
@ImportResource({ "classpath:application-context.xml" })
@ComponentScan("fi.vm.sade")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
