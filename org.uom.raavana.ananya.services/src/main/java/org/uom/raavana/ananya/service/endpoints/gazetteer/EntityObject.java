package org.uom.raavana.ananya.service.endpoints.gazetteer;

import java.util.ArrayList;

public class EntityObject {
	
	String type;
	ArrayList<int[]> offsets;
	String [] texts;
	
	public EntityObject(String type, String [] text, ArrayList<int[]> offsets) {
		this.type=type;
		this.texts =text;
		this.offsets=offsets;
	}

}
