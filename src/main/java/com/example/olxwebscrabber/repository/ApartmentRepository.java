package com.example.olxwebscrabber.repository;

import com.example.olxwebscrabber.entity.Apartment;
import com.example.olxwebscrabber.entity.enums.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, String> {

    List<Apartment> findAllBySourceAndTotalPrice(Source source, Integer totalPrice);
    List<Apartment> findAllByPhoneNumberIsNull();
    List<Apartment> findAllByDataCollectedIsTrue();
}
