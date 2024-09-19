package YUNS_Backend.YUNS.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

public class Notebook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long notebookId;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private LocalDate manufactureDate;

    @Column(nullable = false)
    private String operatingSystem;

    @Column(nullable = false)
    private boolean rentalStatus;

    @Column(nullable = true)
    private String notebookImgUrl;
}
