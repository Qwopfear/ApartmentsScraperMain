package com.example.olxwebscrabber.service.parsingStrategy;

import com.example.olxwebscrabber.entity.Apartment;
import com.example.olxwebscrabber.entity.enums.Type;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.olxwebscrabber.util.StringToNumberParser.toDouble;
import static com.example.olxwebscrabber.util.StringToNumberParser.toInt;

@Service
@RequiredArgsConstructor
public class OtodomService implements ParseService {

    @Override
    public void parse(Document document, Apartment apartment) {
        var title = document.select("h1[data-cy=adPageAdTitle]").first();
        var priceElement = document.select("strong[data-cy=adPageHeaderPrice]").first();
        var rentElement = document.select("div.css-z3xj2a.e1k1vyr25").first();
        var roomElement = document.select("div.css-1ftqasz");
        var contactElement = document.select("p.css-11kgwwy");

        if (title != null) apartment.setTitle(title.text());
        if (priceElement != null) apartment.setPrice(toInt(priceElement.text()));
        if (rentElement != null) apartment.setAdditionalPrice(toInt(rentElement.text()));
        if (contactElement != null) apartment.setOwnerName(contactElement.text());
        if (roomElement != null) {
            parseRoomElement(roomElement).forEach(el -> {
                switch (el.type) {
                    case AREA -> apartment.setArea(toDouble(el.value));
                    case ROOMS -> apartment.setRoomNumber(el.value);
                    case FOR_STUDENTS -> apartment.setForStudents(true);
                    case FOR_SMOKERS -> apartment.setForSmokers(false);
                }
            });
        }

        apartment.setTotalPrice((apartment.getPrice() != null ? apartment.getPrice() : 0)
                + (apartment.getAdditionalPrice() != null ? apartment.getAdditionalPrice() : 0));
        apartment.setType(Type.ofPrice(apartment.getTotalPrice()));

    }

    private List<RoomElement> parseRoomElement(Elements roomElement) {
        return roomElement.stream().map(el -> {
            if (el.text().contains("m²")) return new RoomElement(el.text(), RoomElementType.AREA);
            if (el.text().contains("pok")) return new RoomElement(el.text(), RoomElementType.ROOMS);
            if (el.text().contains("również")) return new RoomElement(el.text(), RoomElementType.FOR_STUDENTS);
            if (el.text().contains("niepalących")) return new RoomElement(el.text(), RoomElementType.FOR_SMOKERS);
            else return null;
        }).toList();
    }

    @AllArgsConstructor
    static class RoomElement {
        String value;
        RoomElementType type;
    }

    enum RoomElementType {
        AREA, ROOMS, FOR_STUDENTS, FOR_SMOKERS
    }
}
