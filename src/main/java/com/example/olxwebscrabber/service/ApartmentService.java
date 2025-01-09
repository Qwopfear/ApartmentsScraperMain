package com.example.olxwebscrabber.service;

import com.example.olxwebscrabber.entity.Apartment;
import com.example.olxwebscrabber.entity.enums.Source;

import java.io.IOException;
import java.util.List;

public interface PageParsingService {

    List<Apartment> collectLinks(String file) throws IOException;

    void collectInfo(Apartment link);

    List<Apartment> getApartmentsWithoutTotalPrice(Source source);
}
