package com.example.olxwebscrabber.service.parsingStrategy;

import com.example.olxwebscrabber.entity.Apartment;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OlxService implements ParseService {

    @Override
    public void parse(Document document, Apartment apartment) {

    }
}
