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

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogConfig;
import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogConfigIF;
import org.productivity.java.syslog4j.impl.net.udp.UDPNetSyslogConfig;

public class SyslogTester {
	public final static int PORT = 514;
	public final static int SPORT = 6514;
	public final static String SYSLOG_INSTANCE_PREFIX = "syslog-instance-";

	public static void send(String protocol, String tAddr, int port, int logLevel, String message) {
		SyslogIF syslog;
		String instanceName = SYSLOG_INSTANCE_PREFIX + protocol + Thread.currentThread().getId();
		if (protocol.toLowerCase().equals("udp")) {
			UDPNetSyslogConfig config = new UDPNetSyslogConfig();
			config.setHost(tAddr);
			config.setPort(port);
			config.setThreaded(false);
			syslog = Syslog.createInstance(instanceName, config);
		} else {
			TCPNetSyslogConfigIF config = new TCPNetSyslogConfig();
			config.setHost(tAddr);
			config.setPort(port);
			config.setThreaded(false);
			syslog = Syslog.createInstance(instanceName, config);
		}
		syslog.log(logLevel, message);
		syslog.flush();
		Syslog.destroyInstance(instanceName);
	}
	
	public static void send(String protocol, String tAddr, int logLevel, String message) {
		send(protocol, tAddr, PORT, logLevel, message);
	}
}
