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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import org.littlestar.event_central.GlobalConnectionFactories;

public class HearbeatDAO {
	private final String dsConfig;
	public HearbeatDAO(String dsConfig) {
		this.dsConfig = dsConfig;
	}
	
	public ArrayList<ListenerInfo> getListeners() throws Throwable {
		String sqlText = "select * from lsec.listeners";
		ArrayList<ListenerInfo> listeners = new ArrayList<ListenerInfo>();
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connection = GlobalConnectionFactories.getConnection(dsConfig);
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sqlText);
			while (rs.next()) {
				ListenerInfo listener = new ListenerInfo();
				listener.setAddr(rs.getString("raddr"));
				listener.setPort(rs.getInt("port"));
				listener.setProtocol(rs.getString("protocol"));
				listener.setType(rs.getString("type"));
				listener.setName(rs.getString("name"));
				String status = rs.getBoolean("status") ? "online" : "offline";
				listener.setStatus(status);
				listener.setLast(rs.getTimestamp("last"));
				listeners.add(listener);
			}
		} catch (Throwable e) {
			throw e;
		} finally {
			try {rs.close();} catch (Throwable e) {}
			try {stmt.close();} catch (Throwable e) {}
			try {connection.close();} catch (Throwable e) {}
		}
		return listeners;
	}
	
	public class ListenerInfo {
		String rAddr;
		int port;
		String protocol;
		String type;
		String name;
		String status;
		Date last;

		public String getAddr() {
			return rAddr;
		}
		
		public void setAddr(String rAddr) {
			this.rAddr = rAddr;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getProtocol() {
			return protocol;
		}

		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public Date getLast() {
			return last;
		}

		public void setLast(Date last) {
			this.last = last;
		}

	}
}
