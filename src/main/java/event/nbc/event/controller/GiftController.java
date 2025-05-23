package event.nbc.event.controller;

import event.nbc.event.repository.EventRedisRepository;
import event.nbc.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Controller
public class GiftController {

    private final EventRedisRepository eventRedisRepository;

    @GetMapping("/event/{eventId}")
    public String enterEventPage(@PathVariable Long eventId, Model model) {
        Event event = eventRedisRepository.findById(eventId);

        LocalDateTime now = Instant.now()
            .atZone(ZoneId.of("Asia/Seoul"))
            .toLocalDateTime();

        boolean isBeforeStart = now.isBefore(event.getStartAt());
        boolean isEnded = now.isAfter(event.getEndAt());
        boolean isAvailable = event.getRemainingCount() > 0 && !isBeforeStart && !isEnded;

        model.addAttribute("event", event);
        model.addAttribute("isBeforeStart", isBeforeStart);
        model.addAttribute("isEnded", isEnded);
        model.addAttribute("isAvailable", isAvailable);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 dd일 HH:mm");

        model.addAttribute("startAt", event.getStartAt().format(formatter));
        model.addAttribute("endAt", event.getEndAt().format(formatter));

        return "gift-page";
    }
}
