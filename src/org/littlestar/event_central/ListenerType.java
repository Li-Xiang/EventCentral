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

public enum ListenerType {
	SNMP(0), SYSLOG(1);
	private final int code;
	ListenerType(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static ListenerType getType(final int code) {
		switch (code) {
		case 0:
			return SNMP;
		case 1:
			return SYSLOG;
		}
		return null;
	}
	
	public static ListenerType getType(final String p) {
		if(SNMP.isEqual(p))
			return SNMP;
		if(SYSLOG.isEqual(p))
			return SYSLOG;
		return null;
	}

	public boolean isEqual(final String name) {
		return name().equalsIgnoreCase(name);
	}
}
