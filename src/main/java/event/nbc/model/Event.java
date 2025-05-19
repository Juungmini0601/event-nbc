package event.nbc.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Event {

	@Getter
    private Long eventId;
	private AtomicLong remainingCount;
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

	public synchronized String tryClaimImageUrlWithChance() {
		if (Math.random() > 0.3) {
			return null;
		}

		long currentCount = remainingCount.get();
		if (currentCount <= 0) return null;

		long imageIndex = imageUrls.size() - currentCount;
		if (imageIndex < 0 || imageIndex >= imageUrls.size()) return null;

		remainingCount.decrementAndGet();
		return imageUrls.get((int) imageIndex);
	}
}
