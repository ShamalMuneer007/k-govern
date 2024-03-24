package org.kerala.kgovern.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long messageId;
    private String message;
    @ManyToOne
    private User user;
    @ManyToOne
    private DepartmentComplaint complaint;
    private String department;
    private String district;
}
