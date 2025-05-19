package event.nbc.event.controller;

import event.nbc.model.Event;
import event.nbc.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class GiftController {

    private final EventRepository eventRepository;

    @GetMapping("/event/{eventId}")
    public String enterEventPage(@PathVariable Long eventId, Model model) {
        Event event = eventRepository.findById(eventId);
        // 나중에 삭제해야함
        if (event == null) {
            event = new Event(
                    eventId,
                    3,
                    List.of(
                            "https://dummyimage.com/300x200/000/fff&text=기프티콘1",
                            "https://dummyimage.com/300x200/111/eee&text=기프티콘2",
                            "https://dummyimage.com/300x200/222/eee&text=기프티콘3"
                    ),
                    LocalDateTime.now().minusMinutes(1),
                    LocalDateTime.now().plusMinutes(10)
            );
            eventRepository.save(event);
        }

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
