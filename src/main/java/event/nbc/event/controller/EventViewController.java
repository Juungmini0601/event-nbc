package event.nbc.event.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EventViewController {
	@GetMapping("/gift-form")
	public String createEventForm() {
		return "gift-form";
	}
}
