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
        addFiles("/ploteus-urls.properties");
        addOptionalFiles(Paths.get(System.getProperties().getProperty("user.home"), "/deployment/oph-configuration/common.properties").toString());
    }

}
