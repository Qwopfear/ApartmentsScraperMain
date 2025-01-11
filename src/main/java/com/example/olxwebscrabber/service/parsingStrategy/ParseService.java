package com.example.olxwebscrabber.service.parsingStrategy;

import com.example.olxwebscrabber.entity.Apartment;
import org.jsoup.nodes.Document;

public interface ParseService {
    void parse(Document document, Apartment apartment);
}
