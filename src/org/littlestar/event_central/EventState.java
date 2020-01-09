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

public enum EventState {
	OPEN(0), CLOSED(1);
	private final int code;

	EventState(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static EventState getState(final int code) {
		switch (code) {
		case 0:
			return OPEN;
		case 1:
			return CLOSED;
		}
		return null;
	}

	public static EventState getState(final String s) {
		if (OPEN.isEqual(s))
			return OPEN;
		if (CLOSED.isEqual(s))
			return CLOSED;
		return null;
	}

	public boolean isEqual(final String name) {
		return name().equalsIgnoreCase(name);
	}
}
