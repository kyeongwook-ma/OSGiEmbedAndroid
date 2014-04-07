package com.example.androidproject.naversearch;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;


@Root
public class Item {
	
	@Element
	private String title;
	
	@Element(required=false)
	private String link;
	
	@Element(required=false)
	private String description;
	
	@Element(required=false)
	private String telephone;
	
	@Element
	private String address;
	
	@Element
	private int mapx;
	
	@Element
	private int mapy;
	
	private double longitude;
	private double latitude;

	@Commit
	public void replaceHtmlTag() {
			
		title = title.replace("<b>", "");
		title = title.replace("</b>", "");
		
		GeoPoint srcPoint = new GeoPoint(mapx, mapy);
		
		GeoPoint dstPoint = GeoTrans.convert(GeoTrans.KATEC, GeoTrans.GEO, srcPoint);
		
		longitude = dstPoint.getX();
		latitude = dstPoint.getY();
	}

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

	public String getDescription() {
		return description;
	}

	public String getTelephone() {
		return telephone;
	}

	public String getAddress() {
		return address;
	}

	public int getMapx() {
		return mapx;
	}

	public int getMapy() {
		return mapy;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	@Override
	public String toString() {
		return "Item [title=" + title + ", link=" + link + ", description="
				+ description + ", telephone=" + telephone + ", address="
				+ address + ", mapx=" + mapx + ", mapy=" + mapy + "]";
	}
}
