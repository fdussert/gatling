package com.excilys.ebi.gatling.recorder.http.event;

import java.util.EventObject;

@SuppressWarnings("serial")
public class TagEvent extends EventObject {

	public TagEvent(String tag) {
		super(tag);
	}

	public String getTag() {
		return String.class.cast(getSource());
	}
}