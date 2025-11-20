package com.rafaelcabanillas.choirapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "song_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Entrada, Kyrie, Gloria...

    @Column(name = "sort_order")
    private Integer order; // 1, 2, 3...
}