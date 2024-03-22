package org.kerala.kgovern.entities;

import jakarta.persistence.*;

@Entity
public class DepartmentMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long messageId;
    private String message;
    @ManyToOne
    private User user;
    private String department;
}
