package event.nbc.model;

import java.time.LocalDateTime;
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

	public synchronized String tryClaimImageUrlWithChance() {
		if (remainingCount <= 0) {
			return "SOLD_OUT";
		}

		if (Math.random() > 0.3) {
			return "FAILED";
		}

		int imageIndex = imageUrls.size() - remainingCount;
		if (imageIndex < 0 || imageIndex >= imageUrls.size()) {
			return "SOLD_OUT";
		}

		remainingCount--;
		return imageUrls.get(imageIndex);
	}
}