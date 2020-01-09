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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.littlestar.event_central.listener.SnmpTrapListener;
import org.littlestar.event_central.listener.SyslogListener;
import org.littlestar.event_central.observer.EventDbLogger;
import org.littlestar.event_central.observer.EventSmLogger;
import org.littlestar.event_central.utils.HeartbeatThread;
import org.littlestar.event_central.utils.LogRecordFormatter;

public class Server {
	private final static Logger logger = Logger.getLogger(Server.class.getName());
	public static final String LOG_FILE_PATH = System.getProperty("user.dir") + File.separator + "lsecs.log";
	List<EventListener> listeners = new Vector<EventListener>();
	
	public Server() {
		try {
			SnmpTrapListener snmpEventListener = new SnmpTrapListener();
			// SyslogListener syslogListener = new SyslogListener("udp","0.0.0.0",514);
			SyslogListener syslogListener = new SyslogListener();
			EventDbLogger dbLogger = new EventDbLogger();
			EventSmLogger smLogger = new EventSmLogger();
			smLogger.setPhoneNumbers("");

			snmpEventListener.addEventObserver(dbLogger);
			snmpEventListener.addEventObserver(smLogger);
			syslogListener.addEventObserver(dbLogger);
			// syslogListener.addEventObserver(smLogger);

			listeners.add(snmpEventListener);
			listeners.add(syslogListener);
			snmpEventListener.startup();
			logger.info(snmpEventListener.getName() + "线程启动...");
			syslogListener.startup();
			logger.info(syslogListener.getName() + "线程启动...");

			HeartbeatThread heartHeatThread = new HeartbeatThread(listeners);
			heartHeatThread.start();
			logger.info("心跳监控线程启动...");
			logger.info("服务器启动...");
			// Thread.sleep(20000L);
			// syslogListener.shutdown();
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "程序初始化失败!", e);
			System.exit(-1);
		}
	}
	
	private static void initLogger(String logPath) throws SecurityException, IOException {
		LogManager.getLogManager().reset();
		Logger rootLogger = Logger.getLogger("");
		rootLogger.setUseParentHandlers(false);
		FileHandler fileHandler = new FileHandler(logPath, 2097152, 4, true); // Append, Max 2 MB, 4 files.
		Formatter logFormatter = new LogRecordFormatter();
		fileHandler.setFormatter(logFormatter);
		fileHandler.setLevel(Level.ALL);
		fileHandler.setEncoding("UTF-8");
		rootLogger.addHandler(fileHandler);
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(logFormatter);
		consoleHandler.setLevel(Level.INFO);
		rootLogger.addHandler(consoleHandler);
		rootLogger.setLevel(Level.INFO);
	}
	
	public static void main(String[] args) throws Throwable {
		initLogger(LOG_FILE_PATH);
		new Server();
	}
}
