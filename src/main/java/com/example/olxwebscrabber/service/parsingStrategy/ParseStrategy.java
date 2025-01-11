package com.example.olxwebscrabber.service.parsingStrategy;

import com.example.olxwebscrabber.entity.enums.Source;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParseStrategy {

    private final Map<String, ParseService> parseServiceMap;

    public ParseService getParser(Source source) {
        return switch (source) {
            case OLX -> parseServiceMap.get("olxService");
            case OTODOM -> parseServiceMap.get("otodomService");
        };
    }

}
