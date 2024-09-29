package YUNS_Backend.YUNS.entity;

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
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long reservationId;

    @Column(nullable = false)
    private LocalDate requestDate;

    @Column(nullable = false)
    private Type type;

    @Column(nullable = false)
    private Long notebookId;

    @Column(nullable = false)
    private Long userId;
}
