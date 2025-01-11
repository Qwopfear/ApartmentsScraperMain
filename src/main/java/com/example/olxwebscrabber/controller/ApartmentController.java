package com.example.olxwebscrabber.controller;

import com.example.olxwebscrabber.dto.PhoneNumberDto;
import com.example.olxwebscrabber.service.ApartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/apartments")
@RequiredArgsConstructor
public class ApartmentController {

    private final ApartmentService apartmentService;

    @PostMapping("/phoneNumber")
    public ResponseEntity<?> updatePhoneNumber(
            @RequestBody PhoneNumberDto dto
    ) {
        apartmentService.setPhoneNumber(dto);
        return ResponseEntity.ok().build();
    }

}
