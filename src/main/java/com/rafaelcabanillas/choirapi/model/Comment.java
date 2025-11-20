package com.rafaelcabanillas.choirapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    private String author; // Name of the user who commented

    // Rich text content for the comment
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> text;

    @Builder.Default
    private OffsetDateTime date = OffsetDateTime.now();
}