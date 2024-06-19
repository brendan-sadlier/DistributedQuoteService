package service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import service.core.Application;
import service.core.ClientInfo;
import service.core.Quotation;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class ApplicationController {

    @Value("${server.port}")
    private int port;

    private ConcurrentHashMap<Integer, Application> applications = new ConcurrentHashMap<>();
    private List<String> quotationServicesUrls = new ArrayList<>();

    @PostMapping(value = "/services", consumes = "application/json")
    public ResponseEntity<Void> registerService(@RequestBody String url) {
        quotationServicesUrls.add(url);
        return ResponseEntity
                .ok()
                .build();
    }

    @GetMapping(value = "/services", produces = "application/json")
    public ResponseEntity<List<String>> getServices() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(quotationServicesUrls);
    }

    @GetMapping(value = "/applications", produces = "application/json")
    public ResponseEntity<ArrayList<String>> getAllApplications() {

        ArrayList<String> list = new ArrayList<>();

        applications.values().forEach(application -> {
            list.add("http:" + getHost() + "/applications/" + application.id);
        });

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);
    }

    @PostMapping(value = "/applications", consumes = "application/json")
    public ResponseEntity<Application> createApplication(@RequestBody ClientInfo info) {

        Application application = new Application(info);
        RestTemplate template = new RestTemplate();

        quotationServicesUrls.forEach(url -> {
            ResponseEntity<String> response = template.postForEntity(url, info, String.class);

            if (response.getStatusCode().equals(HttpStatus.CREATED)) {
                System.out.println("Location of Resource " + response.getHeaders().getLocation().toString());
                try {
                    Quotation quotation = parseQuote(response.getBody());
                    application.quotations.add(quotation);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        applications.put(application.id, application);

        String url = "http://" + getHost() + "/applications/" + application.id;
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", url)
                .header("Content-Location", url)
                .body(application);
    }

    @GetMapping(value = "/applications/{id}", produces = {"application/json"})
    public ResponseEntity<Application> getApplication(@PathVariable int id) {

        Application application = applications.get(id);

        if (application == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(application);
    }

    private String getHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress() + ":" + port;
        } catch (UnknownHostException e) {
            return "localhost:" + port;
        }
    }

    private Quotation parseQuote(String jsonData) throws Exception {
        // Parse the JSON data
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(jsonData, Quotation.class);
    }

}
