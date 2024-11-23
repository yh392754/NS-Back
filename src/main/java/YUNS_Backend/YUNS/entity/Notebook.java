package YUNS_Backend.YUNS.entity;

import YUNS_Backend.YUNS.dto.NotebookRegistRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notebook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long notebookId;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String manufactureDate;

    @Column(nullable = false)
    private String operatingSystem;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RentalStatus rentalStatus;

    @OneToMany(mappedBy = "notebook", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<NotebookImage> images = new ArrayList<>();

    @Column(nullable = false)
    private int size;

    @OneToMany(mappedBy = "notebook", fetch = FetchType.LAZY)
    private List<Rental> rentals;

    public static Notebook createNotebook(String model, String manufactureDate, String os, int size){

        Notebook notebook = Notebook.builder()
                .model(model)
                .manufactureDate(manufactureDate)
                .operatingSystem(os)
                .rentalStatus(RentalStatus.AVAILABLE)
                .size(size)
                .build();

        return notebook;
    }

    public void updateNotebook(String model, String manufactureDate, String os, int size){
        this.model = model;
        this.manufactureDate = manufactureDate;
        this.operatingSystem = os;
        this.size = size;
    }

    public void updateRentalStatus(RentalStatus rentalStatus){
        this.rentalStatus = rentalStatus;
    }

    public void updateImages(List<String> imageUrls) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }
        this.images.clear();
        imageUrls.forEach(url -> this.images.add(new NotebookImage(this, url)));
    }

}
