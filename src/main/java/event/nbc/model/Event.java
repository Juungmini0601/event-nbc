package event.nbc.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

// atomic 은 필요없을 것 같아서 제거했습니다.
@Getter
@Builder
public class Event {
	private Long eventId;
	private Integer remainingCount;
	private List<String> imageUrls;

	private LocalDateTime startAt;
	private LocalDateTime endAt;

	public String getRemainImage() {

		Integer index = --remainingCount;
		return imageUrls.get(index);
	}

}
