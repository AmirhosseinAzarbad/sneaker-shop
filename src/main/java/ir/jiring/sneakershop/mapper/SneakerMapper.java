package ir.jiring.sneakershop.mapper;

import ir.jiring.sneakershop.dto.sneaker.SneakerResponse;
import ir.jiring.sneakershop.models.Sneaker;

public class SneakerMapper {

    public static SneakerResponse toSneakerResponseDTO(Sneaker sneaker) {
        SneakerResponse dto = new SneakerResponse();
        dto.setId(sneaker.getId());
        dto.setName(sneaker.getName());
        dto.setBrand(sneaker.getBrand());
        dto.setPrice(sneaker.getPrice());
        return dto;
    }

}
