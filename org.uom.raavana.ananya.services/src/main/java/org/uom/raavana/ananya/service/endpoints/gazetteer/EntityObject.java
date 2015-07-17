package org.uom.raavana.ananya.service.endpoints.gazetteer;

import java.util.List;

public class EntityObject {
	
	String type;
	List<int[]> offsets;
	String [] texts;
	
	public EntityObject(String type, String [] text, List<int[]> offsets) {
		this.type=type;
		this.texts =text;
		this.offsets=offsets;
	}

}
