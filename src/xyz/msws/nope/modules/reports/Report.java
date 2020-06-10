package xyz.msws.nope.modules.reports;

import java.util.UUID;

import xyz.msws.nope.utils.MSG;

/**
 * Represents a report that a player can make against a target
 * 
 * @author imodm
 *
 */
public class Report {

	private UUID reporter;
	private UUID target;
	private long time;

	private String id;
	private String reason;

	public Report(UUID reporter, UUID target, String reason) {
		this.reporter = reporter;
		this.target = target;
		this.reason = reason;
		this.id = MSG.genUUID(5);
		this.time = System.currentTimeMillis();
	}

	public UUID getReporter() {
		return reporter;
	}

	public UUID getTarget() {
		return target;
	}

	public long getTime() {
		return time;
	}

	public long getAge() {
		return System.currentTimeMillis() - time;
	}

	public String getId() {
		return id;
	}

	public String getReason() {
		return reason;
	}

}
