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

package org.littlestar.event_central.observer;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.littlestar.event_central.GlobalConnectionFactories;
import org.littlestar.event_central.Event;
import org.littlestar.event_central.EventObserver;
import org.littlestar.event_central.Server;

public class EventDbLogger implements EventObserver {
	private final static Logger logger = Logger.getLogger(Server.class.getName());
	private final DbWriter dbWriter;

	public EventDbLogger() {
		dbWriter = new DbWriter();
		dbWriter.start();
	}

	@Override
	public void eventReceived(Event event) {
		dbWriter.put(event);
	}

	@Override
	public void close() {
		dbWriter.stopWriter();
	}

	private static class DbWriter extends Thread implements Closeable {
		private volatile boolean running = true;
		private Connection connection;
		private final BlockingQueue<Event> writeQueue = new LinkedBlockingQueue<Event>(500);

		public void put(Event event) {
			try {
				writeQueue.put(event);
			} catch (Throwable e) {
				logger.log(Level.WARNING, "事件加入数据库写队列失败!", e);
			}
		}

		@Override
		public void run() {
			while (running) {
				try {
					Event event = writeQueue.take();
					write(event);
				} catch (Throwable e) {
					logger.log(Level.WARNING, "事件写入数据库失败!", e);
				}
			}
			close();
		}

		private int write(Event event) throws Throwable {
			if (event == null)
				return 0;
			if (connection == null) {
				connection = GlobalConnectionFactories.getConnection();
			} else {
				if (connection.isClosed())
					connection = GlobalConnectionFactories.getConnection();
			}
			String sqlText = "insert into lsec.events(created,severity,type,source,state,data, digest) values(?,?,?,?,?,?,?)";
			PreparedStatement stmt = connection.prepareStatement(sqlText);
			stmt.setTimestamp(1, new Timestamp(event.getTimestamp().getTime()));
			stmt.setString(2, event.getSeverity().toString());
			stmt.setString(3, event.getListenerType().toString());
			stmt.setString(4, event.getSource());
			stmt.setString(5, event.getState().toString());
			stmt.setString(6, event.getData());
			stmt.setString(7, event.getDigest());
			int row = stmt.executeUpdate();
			stmt.close();
			connection.close();
			return row;
		}

		public void stopWriter() {
			running = false;
		}

		@Override
		public void close() {
			if (connection != null) {
				try {
					connection.close();
				} catch (Throwable e) {
					logger.log(Level.WARNING, "", e);
				}
			}
		}
	}

}
