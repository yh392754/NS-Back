package YUNS_Backend.YUNS.entity;

import YUNS_Backend.YUNS.dto.NotebookRegistRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private LocalDate manufactureDate;

    @Column(nullable = false)
    private String operatingSystem;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RentalStatus rentalStatus;

    @Column(nullable = true)
    private String notebookImgUrl;

    @Column(nullable = false)
    private int size;

    public static Notebook createNotebook(NotebookRegistRequestDto notebookRegistRequestDto, String imgUrl){

        Notebook notebook = Notebook.builder()
                .model(notebookRegistRequestDto.getModel())
                .manufactureDate(notebookRegistRequestDto.getManufactureDate())
                .operatingSystem(notebookRegistRequestDto.getOs())
                .notebookImgUrl(imgUrl)
                .rentalStatus(RentalStatus.AVAILABLE)
                .size(notebookRegistRequestDto.getSize())
                .build();

        return notebook;
    }

    public void updateNotebook(NotebookRegistRequestDto notebookRegistRequestDto, String imgUrl){
        this.model = notebookRegistRequestDto.getModel();
        this.manufactureDate = notebookRegistRequestDto.getManufactureDate();
        this.operatingSystem = notebookRegistRequestDto.getOs();
        this.notebookImgUrl = imgUrl;
        this.size = notebookRegistRequestDto.getSize();
    }

    public void updateRentalStatus(RentalStatus rentalStatus){
        this.rentalStatus = rentalStatus;
    }
}
