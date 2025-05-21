package event.nbc.event.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import event.nbc.event.dto.EventSetRequest;
import event.nbc.event.service.EventService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EventController {

	private final EventService nbcService;

	@PostMapping("/gift")
	public ResponseEntity<List<String>> setEvent(@RequestBody EventSetRequest event) {

		return ResponseEntity.ok(nbcService.setEvent(event));
	}

	@GetMapping("/gift")
	public ResponseEntity<byte[]> getEvent(@RequestParam Long eventId) {

		return ResponseEntity
			.ok()
			.contentType(MediaType.IMAGE_PNG)
			.body(nbcService.getEvent(eventId));
	}

}
