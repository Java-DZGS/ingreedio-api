package pl.edu.pw.mini.ingreedio.api.dto;

public record ProductDto(
        Long id,
        String name,
        String smallImageUrl,
        String provider,
        String shortDescription
){

}
