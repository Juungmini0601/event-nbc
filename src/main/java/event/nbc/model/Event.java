package event.nbc.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Event {

	@Getter
    private Long eventId;
	private AtomicLong remainingCount;
	@Getter
	private List<String> imageUrls; // ConcurrentLinkedQueue, LinkedBlockingQueue, ArrayBlockingQueue, PriorityBlockingQueue, SynchronousQueue

	@Getter
    private LocalDateTime startAt;
	@Getter
    private LocalDateTime endAt;

	public Event(Long eventId, int remainingCount, List<String> imageUrls,
				 LocalDateTime startAt, LocalDateTime endAt) {
		this.eventId = eventId;
		this.remainingCount = new AtomicLong(remainingCount);
		this.imageUrls = imageUrls;
		this.startAt = startAt;
		this.endAt = endAt;
	}

    public long getRemainingCount() {
		return remainingCount.get();
	}

	public void decreaseRemakingCount(){
		remainingCount.decrementAndGet();
	}
	public synchronized String tryClaimImageUrlWithChance() {
		if (remainingCount.get() <= 0) {
			return "SOLD_OUT";
		}

		if (Math.random() > 0.3) {
			return "FAILED";
		}

		long imageIndex = imageUrls.size() - remainingCount.get();
		if (imageIndex < 0 || imageIndex >= imageUrls.size()) {
			return "SOLD_OUT";
		}

		remainingCount.decrementAndGet();
		return imageUrls.get((int) imageIndex);
	}

	public boolean isEventActive() {
		LocalDateTime now = LocalDateTime.now();
		return !now.isBefore(this.startAt) && !now.isAfter(this.endAt);
	}

}
