package com.example.olxwebscrabber.service;

import com.example.olxwebscrabber.entity.Apartment;
import com.example.olxwebscrabber.entity.enums.Source;
import com.example.olxwebscrabber.repository.ApartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageParsingServiceImpl implements PageParsingService {

    private final ApartmentRepository repository;
    private final OtodomService otodomService;

    @Value("${olx.main.url}")
    private String olxUrl;


    @Override
    public List<Apartment> collectLinks(String file) throws IOException {
        try {
            Connection connection = Jsoup.connect(file);
            var doc = connection.get();
            Elements links = doc.select("a[href]");
            return repository.saveAll(
                    links.stream()
                            .map(el -> {
                                var val = el.attribute("href").getValue();
                                return val.startsWith("/d/oferta") ? olxUrl + val : val;
                            })
                            .filter(el -> el.contains("/d/oferta") || el.startsWith("https://www.otodom.pl/pl/oferta"))
                            .distinct()
                            .map(el -> Apartment.builder()
                                    .id(el)
                                    .source(Source.defineByPrefix(el))
                                    .dataCollected(false)
                                    .build())
                            .toList()
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Ex");
        }
    }

    @Override
    @SneakyThrows
    public void collectInfo(Apartment apartment) {
        Document document = Jsoup.connect(apartment.getId()).get();
        switch (apartment.getSource()) {
            case OLX -> {}
            case OTODOM -> otodomService.parse(document, apartment);
        }

        repository.save(apartment);
    }

    @Override
    public List<Apartment> getApartmentsWithoutTotalPrice(Source source) {
        return repository.findAllBySourceAndTotalPrice(source, null);
    }

}
