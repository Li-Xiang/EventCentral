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

package org.littlestar.event_central.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.littlestar.event_central.Event;
import org.littlestar.event_central.EventState;
import org.littlestar.event_central.GlobalConnectionFactories;
import org.littlestar.event_central.ListenerType;
import org.littlestar.event_central.utils.PduJsonWrap;
import org.productivity.java.syslog4j.server.impl.event.SyslogServerEvent;

import com.google.gson.Gson;

public class EventTester {
	private final static Logger logger = Logger.getLogger(EventTester.class.getName());
	public static void send(String protocol, String tAddr, String eIds) throws Throwable {
		List<Event> events = getEvents(eIds);
		SnmpTester snmpTester = null;
		for (Event event : events) {
			ListenerType lType = event.getListenerType();
			Gson gson = new Gson();
			String jsonData = event.getData();
			if (lType.equals(ListenerType.SNMP)) {
				if (snmpTester == null) {
					snmpTester = new SnmpTester(protocol);
				}
				PduJsonWrap pduWrap = gson.fromJson(jsonData, PduJsonWrap.class);
				snmpTester.send(pduWrap.getPDU(), tAddr);
			} else if (event.getListenerType().equals(ListenerType.SYSLOG)) {
				SyslogServerEvent serverEvent = gson.fromJson(jsonData, SyslogServerEvent.class);
				int facility = serverEvent.getFacility();
				//String charset = serverEvent.getCharSet();
				//String messages = new String(serverEvent.getRaw(), Charset.forName(charset));
				String messages = serverEvent.getMessage();
				SyslogTester.send(protocol, tAddr, facility, messages);
			}
		}
	}
	
	public static void send(String protocol, String tAddr, int[] eIds) throws Throwable {
		String strIds = toString(eIds);
		send(protocol, tAddr, strIds);
	}
	
	public static List<Event> getEvents(String eIds) throws Throwable{
		List<Event> events = new Vector<Event>();
		final Connection connection = GlobalConnectionFactories.getConnection();
		final String sqlText = "select * from lsec.events where id in ("+eIds+")";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sqlText);
		while(rs.next()){
			Event event = new Event();
			event.setEventId(rs.getInt("id"));
			event.setTimestamp(rs.getTimestamp("created"));
			event.setListenerType(ListenerType.getType(rs.getString("type")));
			event.setSource(rs.getString("source"));
			event.setState(EventState.getState(rs.getString("state")));
			event.setData(rs.getString("data"));
			event.setDigest(rs.getString("digest"));
			events.add(event);
		}
		return events;
	}
	
	public static String toString(int[] events) {
		String strArray = Arrays.toString(events);
		return strArray.substring(1, strArray.length() - 1);
	}

	public static void main(String[] args) throws Throwable {
		if (args.length < 3) {
			logger.info("必须参数: {tcp|udp} {target-address} {event-ids(事件编号,逗号分隔)}");
			System.exit(-1);
		}
		EventTester.send(args[0], args[1], args[2]);
	}
}
