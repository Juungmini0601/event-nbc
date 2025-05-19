package event.nbc.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Event {
	private Long eventId;
	private AtomicLong remainingCount;
	private List<String> imageUrls; // ConcurrentLinkedQueue, LinkedBlockingQueue, ArrayBlockingQueue, PriorityBlockingQueue, SynchronousQueue

	private LocalDateTime startAt;
	private LocalDateTime endAt;
}
