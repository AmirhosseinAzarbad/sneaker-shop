package ir.jiring.sneakershop.mapper;

import ir.jiring.sneakershop.dto.variant.SneakerVariantAddAndUpdateResponse;
import ir.jiring.sneakershop.dto.variant.SneakerVariantGetAllResponse;
import ir.jiring.sneakershop.models.Sneaker;
import ir.jiring.sneakershop.models.SneakerVariant;

import java.util.List;
import java.util.stream.Collectors;

public class SneakerVariantMapper {
    public static SneakerVariantAddAndUpdateResponse toAddAndUpdateVariantResponseDTO(SneakerVariant variant, Sneaker sneaker) {
        SneakerVariantAddAndUpdateResponse dto = new SneakerVariantAddAndUpdateResponse();
        dto.setSneakerName(sneaker.getName());
        dto.setSneakerBrand(sneaker.getBrand());
        dto.setId(variant.getId());
        dto.setColor(variant.getColor());
        dto.setSize(variant.getSize());
        dto.setPrice(variant.getPrice());
        dto.setStockQuantity(variant.getStockQuantity());
        return dto;
    }

    public static SneakerVariantGetAllResponse toGetAllVariantResponseDTO(Sneaker sneaker, List<SneakerVariant> variants) {
        SneakerVariantGetAllResponse response = new SneakerVariantGetAllResponse();
        response.setSneakerName(sneaker.getName());
        response.setSneakerBrand(sneaker.getBrand());
        response.setVariants(
                variants.stream().map(v -> {
                    SneakerVariantGetAllResponse.VariantsList dto = new SneakerVariantGetAllResponse.VariantsList();
                    dto.setId(v.getId());
                    dto.setColor(v.getColor());
                    dto.setSize(v.getSize());
                    dto.setStockQuantity(v.getStockQuantity());
                    dto.setPrice(v.getPrice());
                    return dto;
                }).collect(Collectors.toList()));
        return response;
    }
}
