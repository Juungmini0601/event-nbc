package event.nbc.domain.event.controller;

import event.nbc.domain.service.EventParticipationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ClaimController {

    private final EventParticipationUseCase eventParticipationUseCase;

    @PostMapping("/event/{eventId}/claim")
    public ResponseEntity<byte[]> eventParticipate(
            @PathVariable Long eventId
    ){
        return ResponseEntity.ok()
                .body(eventParticipationUseCase.participateEvent(eventId));
    }
}
