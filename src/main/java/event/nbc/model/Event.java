package event.nbc.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Event {
	private Long eventId;
	private Integer remainingCount;
	private List<String> imageUrls;

	private LocalDateTime startAt;
	private LocalDateTime endAt;

	@JsonIgnore
	public String getRemainImage() {

		int index = --remainingCount;

		if (index <= -1)
			throw new RuntimeException("기프티콘 전량 소진");

		return imageUrls.get(index);
	}

}
