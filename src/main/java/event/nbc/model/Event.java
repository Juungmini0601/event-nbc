package event.nbc.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
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

	public Event(Long eventId, int remainingCount, List<String> imageUrls,
		LocalDateTime startAt, LocalDateTime endAt) {
		this.eventId = eventId;
		this.remainingCount = remainingCount;
		this.imageUrls = imageUrls;
		this.startAt = startAt;
		this.endAt = endAt;
	}

	public int getRemainingCount() {
		return remainingCount;
	}

	public void decreaseRemainingCount(){
		remainingCount--;
	}

	public boolean isEventActive() {
		LocalDateTime now = Instant.now()
			.atZone(ZoneId.of("Asia/Seoul"))
			.toLocalDateTime();

		return !now.isBefore(this.startAt) && !now.isAfter(this.endAt);
	}

}
