package com.example.olxwebscrabber.schedule;

import com.example.olxwebscrabber.entity.Apartment;
import com.example.olxwebscrabber.entity.enums.Source;
import com.example.olxwebscrabber.service.ApartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

@Component
@RequiredArgsConstructor
public class WebParsingScheduler {

    private final ApartmentService apartmentService;
    private final ExecutorService phoneNumberParsingExecutor;
    private final ExecutorService pageParsingExecutor;

    private final Semaphore phoneNumberProcessSemaphore = new Semaphore(5);


    @Value("${olx.apps.url}")
    private String olxUrl;

    @Scheduled(fixedRate = 15000)
    public void scheduleOlxParsing() {
        try {
            List<Apartment> apartments = apartmentService.collectLinks(olxUrl);
            apartments.forEach(el -> pageParsingExecutor.submit(() -> apartmentService.collectInfo(el)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(fixedRate = 15000)
    public void scheduledRetry() {
        try {
            List<Apartment> withMissingPrice = apartmentService.getApartmentsWithoutTotalPrice(Source.OTODOM);
            withMissingPrice.forEach(el -> pageParsingExecutor.submit(() -> apartmentService.collectInfo(el)));
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

//    @Scheduled(fixedRate = 10_000)
//    public void parsePhoneNumbers() {
//        var apartments = apartmentService.getApartmentsWithoutPhoneNumber();
//        apartments.forEach(el -> phoneNumberParsingExecutor.submit(() -> runPhoneNumberProcess(el.getId(), el.getSource())));
//
//    }


    private void runPhoneNumberProcess(String link, Source source) {
        // Script is only for otodom for now
        if (!source.equals(Source.OTODOM)) return;
        if (!phoneNumberProcessSemaphore.tryAcquire()) return;

        var basePath = System.getProperty("user.dir");
        var scriptPath = basePath + "/js/main.js";

        ProcessBuilder processBuilder = new ProcessBuilder("node", scriptPath, link);

        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            var process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            phoneNumberProcessSemaphore.release();
        }
    }
}
