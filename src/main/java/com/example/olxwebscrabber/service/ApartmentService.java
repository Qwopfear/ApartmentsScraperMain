package com.example.olxwebscrabber.service;

import com.example.olxwebscrabber.dto.PhoneNumberDto;
import com.example.olxwebscrabber.entity.Apartment;
import com.example.olxwebscrabber.entity.enums.Source;

import java.io.IOException;
import java.util.List;

public interface ApartmentService {


    void collectInfo(Apartment link);
    void setPhoneNumber(PhoneNumberDto dto);

    List<Apartment> collectLinks(String file) throws IOException;
    List<Apartment> getApartmentsWithoutTotalPrice(Source source);
    List<Apartment> getApartmentsWithoutPhoneNumber();
}
