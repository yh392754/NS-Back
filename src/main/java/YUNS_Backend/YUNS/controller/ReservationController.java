package YUNS_Backend.YUNS.controller;

import YUNS_Backend.YUNS.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping(value = "/api/rentals/create/{notebookId}")
    public ResponseEntity<Object> rental(@PathVariable("notebookId") Long notebookId, Principal principal){

        String studentNumber = principal.getName();

        reservationService.rental(notebookId, studentNumber);

        Map<String, String> response = new HashMap<>();
        response.put("message", "대여 신청이 완료되었습니다.");

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/extends/create/{notebookId}")
    public ResponseEntity<Object> extend(@PathVariable("notebookId") Long notebookId, Principal principal){

        String studentNumber = principal.getName();

        reservationService.extend(notebookId, studentNumber);

        Map<String, String> response = new HashMap<>();
        response.put("message", "연장 신청이 완료되었습니다.");

        return ResponseEntity.ok(response);
    }
}
