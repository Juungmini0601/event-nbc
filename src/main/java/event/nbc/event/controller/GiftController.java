package event.nbc.event.controller;

import event.nbc.event.repository.EventRedisRepository;
import event.nbc.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Controller
public class GiftController {

    private final EventRedisRepository eventRedisRepository;

    @GetMapping("/event/{eventId}")
    public String enterEventPage(@PathVariable Long eventId, Model model) {
        Event event = eventRedisRepository.findById(eventId);

        LocalDateTime now = LocalDateTime.now();
        boolean isBeforeStart = now.isBefore(event.getStartAt());
        boolean isEnded = now.isAfter(event.getEndAt());
        boolean isAvailable = event.getRemainingCount() > 0 && !isBeforeStart && !isEnded;

        model.addAttribute("event", event);
        model.addAttribute("isBeforeStart", isBeforeStart);
        model.addAttribute("isEnded", isEnded);
        model.addAttribute("isAvailable", isAvailable);

        return "gift-page";
    }
}
