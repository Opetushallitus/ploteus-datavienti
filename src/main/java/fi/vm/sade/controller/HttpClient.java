package fi.vm.sade.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.javautils.httpclient.ApacheOphHttpClient;
import fi.vm.sade.javautils.httpclient.OphHttpClient;
import fi.vm.sade.properties.OphProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HttpClient {

    private OphHttpClient client;

    @Autowired
    public HttpClient(OphProperties urlConfiguration) {
        client = ApacheOphHttpClient.createDefaultOphHttpClient("ploteus.backend", urlConfiguration, 60000, 600);
    }

    public static ObjectMapper createJacksonMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    public OphHttpClient getClient() {
        return client;
    }
}
