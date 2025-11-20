package com.rafaelcabanillas.choirapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "songs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    // --- THE MAGIC PART ---
    // This maps your "JSONContent" from React directly to a JSONB column in Postgres.
    // We use Map<String, Object> as a generic container for the JSON structure.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> content;

    private String type; // Generic string type or relation?
    private String composer;

    @ManyToOne
    @JoinColumn(name = "song_type_id")
    private SongType songType;
}