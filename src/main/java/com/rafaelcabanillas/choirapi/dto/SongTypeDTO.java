package com.rafaelcabanillas.choirapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SongTypeDTO {
    private Long id;
    private String name;
    private Integer order;
}