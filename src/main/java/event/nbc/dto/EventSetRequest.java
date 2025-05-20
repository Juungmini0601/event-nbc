package event.nbc.dto;

import java.time.LocalDateTime;
import java.util.List;

public record EventSetRequest(

	Long eventId,
	List<String> imageName,

	LocalDateTime startAt,
	LocalDateTime endAt
) {

}
