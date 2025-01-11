package com.example.olxwebscrabber.entity;


import com.example.olxwebscrabber.entity.enums.Source;
import com.example.olxwebscrabber.entity.enums.Type;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@Table
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Apartment {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private Source source;
    @Enumerated(EnumType.STRING)
    private Type type;

    private String title;
    private String roomNumber;
    private String ownerName;
    private String phoneNumber;

    private Integer price;
    private Integer additionalPrice;
    private Integer totalPrice;
    private Double area;

    private Boolean dataCollected = false;
    private Boolean forStudents;
    private Boolean forSmokers;

    private LocalDate createdAt;

    @PrePersist
    public void prePersist() {
        if (this.forStudents == null) this.forStudents = false;
        if (this.forSmokers == null) this.forSmokers = true;
    }

}
