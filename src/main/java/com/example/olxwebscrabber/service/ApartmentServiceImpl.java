package com.example.olxwebscrabber.service;

import com.example.olxwebscrabber.config.RabbitMqConfig;
import com.example.olxwebscrabber.dto.PhoneNumberDto;
import com.example.olxwebscrabber.entity.Apartment;
import com.example.olxwebscrabber.entity.enums.Source;
import com.example.olxwebscrabber.repository.ApartmentRepository;
import com.example.olxwebscrabber.service.parsingStrategy.ParseService;
import com.example.olxwebscrabber.service.parsingStrategy.ParseStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApartmentServiceImpl implements ApartmentService {

    private final ApartmentRepository repository;
    private final ParseStrategy parseStrategy;
    private final RabbitTemplate rabbitTemplate;

    @Value("${olx.main.url}")
    private String olxUrl;

    @Override
    @Transactional
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
                                    .createdAt(LocalDate.now())
                                    .build())
                            .toList()
            );
        } catch (Exception e) {
            throw new RuntimeException("Ex");
        }
    }

    @Override
    @SneakyThrows
    public void collectInfo(Apartment apartment) {
        Document document = Jsoup.connect(apartment.getId()).get();
        parseStrategy.getParser(apartment.getSource()).parse(document, apartment);
        repository.save(apartment);
    }

    @Override
    public List<Apartment> getApartmentsWithoutTotalPrice(Source source) {
        return repository.findAllBySourceAndTotalPrice(source, null);
    }

    @Override
    @Transactional
    public void setPhoneNumber(PhoneNumberDto dto) {
        var apartment = repository.findById(dto.link()).orElseThrow(() -> new RuntimeException(
                String.format("Apartment is not found %s", dto.link())));
        apartment.setPhoneNumber(dto.phoneNumber());
        apartment.setDataCollected(true);
        repository.save(apartment);
        sendDataToQueue(apartment);
    }

    @Override
    public List<Apartment> getApartmentsWithoutPhoneNumber() {
        return repository.findAllByPhoneNumberIsNull();
    }

    private void sendDataToQueue(Apartment apartment) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.ROUTING_KEY,
                apartment
        );
    }

}
