package org.kerala.kgovern.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentNews {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long newsId;
    private String newsHeading;
    private String newsContent;
    private LocalDate expiryDateTime;
    private String department;
    private String district;
    private String image;
}
