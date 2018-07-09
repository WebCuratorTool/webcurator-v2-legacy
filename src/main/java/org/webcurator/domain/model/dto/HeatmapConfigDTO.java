package org.webcurator.domain.model.dto;

public class HeatmapConfigDTO {
	private String name;
	private String displayName;
	private String color;
	private int thresholdLowest;
	private Long oid;

	public HeatmapConfigDTO(Long oid, String name, String displayName, String color,
			int thresholdLowest) {
		this.oid = oid;
		this.name = name;
		this.displayName = displayName;
		this.color = color;
		this.thresholdLowest = thresholdLowest;
	}

	public HeatmapConfigDTO() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getThresholdLowest() {
		return thresholdLowest;
	}

	public void setThresholdLowest(int thresholdLowest) {
		this.thresholdLowest = thresholdLowest;
	}

	public Long getOid() {
		return oid;
	}

	public void setOid(Long oid) {
		this.oid = oid;
	}

}
