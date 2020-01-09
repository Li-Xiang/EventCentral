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

package org.littlestar.event_central.webui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.littlestar.event_central.Event;
import org.littlestar.event_central.EventState;
import org.littlestar.event_central.GlobalConnectionFactories;
import org.littlestar.event_central.ListenerType;
import org.littlestar.event_central.Severity;

public class EventDAO {
	private final String dsConfig;
	public EventDAO(String dsConfig) {
		this.dsConfig = dsConfig;
	}
	public int closeEvent(int id) {
		int count = 0;
		String sqlText = "update lsec.events set state='" + EventState.CLOSED + "' where id=?";
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			connection = GlobalConnectionFactories.getConnection(dsConfig);
			stmt = connection.prepareStatement(sqlText);
			stmt.setInt(1, id);
			count = stmt.executeUpdate();
		} catch (Throwable e) {
		} finally {
			try {stmt.close();} catch (Throwable e) {}
			try {connection.close();} catch (Throwable e) {}
		}
		return count;
	}
	
	public int getEventCount(boolean openOnly) {
		int count = 0;
		String sqlText = "select count(*) cnt  from lsec.events ";
		if (openOnly) {
			sqlText += " where state='" + EventState.OPEN + "'";
		}
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connection = GlobalConnectionFactories.getConnection(dsConfig);
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sqlText);
			if (rs.next()) {
				count = rs.getInt("cnt");
			}
		} catch (Throwable e) {
		} finally {
			try { rs.close();}catch(Throwable e) {}
			try { stmt.close();}catch(Throwable e) {}
			try { connection.close();}catch(Throwable e) {}
		}
		return count;
	}
	
	public List<Event> getEvents(int start, int length, boolean openOnly) throws Throwable {
		String sqlText = 
				"SELECT e.id, "+
				"       e.created, "+
				"       e.severity, "+
				"       e.type, "+
				"       IF(h.name IS NULL,e.source,concat(h.name,' (',e.source,')')) source, "+
				"       e.state, "+
				"       e.data, "+
				"       e.digest "+
				"FROM lsec.events e LEFT JOIN lsec.hosts h ON e.source = h.addr ";
		if (openOnly) {
			sqlText += "where state='" + EventState.OPEN + "' ";
		}
		sqlText += "order by id desc limit " + start + ", " + length;
		ArrayList<Event> events = new ArrayList<Event>();
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connection = GlobalConnectionFactories.getConnection(dsConfig);
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sqlText);
			while (rs.next()) {
				Event event = new Event();
				event.setEventId(rs.getInt("id"));
				event.setTimestamp(rs.getTimestamp("created"));
				event.setSeverity(Severity.getSeverity(rs.getString("severity")));
				event.setListenerType(ListenerType.getType(rs.getString("type")));
				event.setSource(rs.getString("source"));
				event.setState(EventState.getState(rs.getString("state")));
				event.setData(rs.getString("data"));
				event.setDigest(rs.getString("digest"));
				events.add(event);
			}
		} catch (Throwable e) {
			throw e;
		} finally {
			try { rs.close();}catch(Throwable e) {}
			try { stmt.close();}catch(Throwable e) {}
			try { connection.close();}catch(Throwable e) {}
		}
		return events;
	}
}
