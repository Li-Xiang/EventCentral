/*
 * Copyright 2019 Li Xiang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.littlestar.event_central;

import java.util.Date;

import org.littlestar.event_central.utils.PduHelper;

public class Event {
	public static final int EVENT_DIGEST_LENGTH = 128;
	private int eventId = -1;
	private Date timestamp;
	private Severity severity = Severity.INFO;
	private ListenerType type;
	private String source;
	private EventState state;
	private String data;
	private String digest;

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	public ListenerType getListenerType() {
		return type;
	}

	public void setListenerType(ListenerType type) {
		this.type = type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setState(EventState state) {
		this.state = state;
	}

	public EventState getState() {
		return state;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = PduHelper.truncString(digest, EVENT_DIGEST_LENGTH);
	}
	
}