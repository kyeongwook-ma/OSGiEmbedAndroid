package com.example.androidproject.naversearch;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Channel {
	
	@Element
	private String title;
	
	@Element
	private String link;
	
	@Element
	private String description;
	
	@Element
	private String lastBuildDate;
	
	@Element
	private int total;
	
	@Element
	private int start;
	
	@Element
	private int display;
	
	@ElementList(inline=true, required=false)
	private List<Item> items;

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

	public String getDescription() {
		return description;
	}

	public String getLastBuildDate() {
		return lastBuildDate;
	}

	public int getTotal() {
		return total;
	}

	public int getStart() {
		return start;
	}

	public int getDisplay() {
		return display;
	}

	public List<Item> getItems() {
		return items;
	}

	@Override
	public String toString() {
		return "Channel [title=" + title + ", link=" + link + ", description="
				+ description + ", lastBuildDate=" + lastBuildDate + ", total="
				+ total + ", start=" + start + ", display=" + display
				+ ", items=" + items + "]";
	}
}