package fi.vm.sade.ploteus;

import fi.vm.sade.properties.OphProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

@Configuration
public class UrlConfiguration extends OphProperties {

    public UrlConfiguration() {
        this(System.getProperty("spring.profiles.active"));
    }

    public UrlConfiguration(String activeSpringProfile) {
        addFiles("/ploteus-oph.properties");
        addOptionalFiles(Paths.get(System.getProperties().getProperty("user.home"), "/oph-configuration/common.properties").toString());
        //debugMode();
    }

}
