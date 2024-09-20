package YUNS_Backend.YUNS.entity;

import YUNS_Backend.YUNS.dto.NotebookDto;
import YUNS_Backend.YUNS.dto.UserRegisterDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private boolean rentalStatus;

    @Column(nullable = true)
    private String notebookImgUrl;

    public static Notebook createNotebook(NotebookDto notebookDto, String imgUrl){

        Notebook notebook = Notebook.builder()
                .model(notebookDto.getModel())
                .manufactureDate(notebookDto.getManufactureDate())
                .operatingSystem(notebookDto.getOs())
                .notebookImgUrl(imgUrl)
                .rentalStatus(false)
                .build();

        return notebook;
    }

    public void updateNotebook(NotebookDto notebookDto, String imgUrl){
        this.model = notebookDto.getModel();
        this.manufactureDate = notebookDto.getManufactureDate();
        this.operatingSystem = notebookDto.getOs();
        this.notebookImgUrl = imgUrl;
    }
}
