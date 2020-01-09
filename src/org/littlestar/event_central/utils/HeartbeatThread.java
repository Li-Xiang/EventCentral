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

package org.littlestar.event_central.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.littlestar.event_central.EventListener;
import org.littlestar.event_central.GlobalConnectionFactories;

public class HeartbeatThread extends Thread {
	private final static Logger logger = Logger.getLogger(HeartbeatThread.class.getName());
	private final List<EventListener> listeners;
	private long interval = 5000L;
	private volatile boolean running = true;
	
	public HeartbeatThread(List<EventListener> listeners) {
		this.listeners = listeners;
	}
	
	public void setInterval(long interval) {
		this.interval = interval;
	}
	
	public void stopHeartbeat() {
		running = false;
	}
	
	public void writeHB(EventListener listener) throws Throwable {
		String sqlText = "select substring_index(host,':', 1) host from information_schema.processlist WHERE ID=connection_id()";
		int port = listener.getPort();
		String protocol = listener.getProtocol();
		String type = listener.getType().toString();
		String name = listener.getName();
		boolean status = listener.isListening();
		Date last = new Date();
		String rAddr = "?";
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connection = GlobalConnectionFactories.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sqlText);
			if (rs.next()) {
				rAddr = rs.getString("host");
			}
		} catch (Throwable e) {
			throw e;
		} finally {
			try {rs.close();} catch (Throwable e) {}
			try {stmt.close();} catch (Throwable e) {}
		}
		sqlText = "replace into lsec.listeners(raddr, port, protocol, type, name, status, last) values(?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement pStmt = null;
		try {
			pStmt = connection.prepareStatement(sqlText);
			pStmt.setString(1, rAddr);
			pStmt.setInt(2, port);
			pStmt.setString(3, protocol);
			pStmt.setString(4, type);
			pStmt.setString(5, name);
			pStmt.setBoolean(6, status);
			pStmt.setTimestamp(7, new Timestamp(last.getTime()));
			pStmt.executeUpdate();
		} catch (Throwable e) {
			throw e;
		} finally {
			try {connection.close();} catch (Throwable e) {}
			try {pStmt.close();} catch (Throwable e) {}
		}
	}
	
	@Override
	public void run() {
		while (running) {
			for (EventListener listener : listeners) {
				try {
					writeHB(listener);
				} catch (Throwable e) {
					logger.log(Level.WARNING, "写心跳失败!", e);
				}
			}
			try {
				Thread.sleep(interval);
			} catch (Throwable e) {
			}
		}
	}
}
