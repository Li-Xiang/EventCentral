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

package org.littlestar.event_central.listener;

import java.net.SocketAddress;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.littlestar.event_central.Event;
import org.littlestar.event_central.EventListener;
import org.littlestar.event_central.EventObserver;
import org.littlestar.event_central.EventState;
import org.littlestar.event_central.ListenerType;
import org.littlestar.event_central.Severity;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.SyslogServerSessionEventHandlerIF;
import org.productivity.java.syslog4j.server.impl.net.tcp.TCPNetSyslogServerConfig;
import org.productivity.java.syslog4j.server.impl.net.udp.UDPNetSyslogServerConfig;

import com.google.gson.Gson;

//syslog4j 0.9.46+ 
public class SyslogListener implements EventListener, SyslogServerSessionEventHandlerIF  {
	private static final long serialVersionUID = -5763230439751878425L;
	private final SyslogServerConfigIF syslogConfig;
	/*
	 * Default Port TCP/UDP : 514 
	 * Secure Encrypted Port TCP: 6514
	 */
	private String host     = "0.0.0.0";
	private int    port     = 514;
	private String protocol = "tcp";
	
	public SyslogListener(String protocol, String host, int port) {
		this.protocol = protocol;
		this.host = host;
		this.port = port;
		syslogConfig = getSyslogServerConfig(protocol, host, port);
	}

	public SyslogListener() {
		syslogConfig = getSyslogServerConfig(protocol, host, port);
	}
	
	private SyslogServerConfigIF getSyslogServerConfig(String protocol, String host, int port) {
		SyslogServer.shutdown();
		SyslogServerConfigIF serverConfig = null;
		protocol = protocol.trim().toLowerCase();
		if (protocol.equals("udp")) {
			serverConfig = new UDPNetSyslogServerConfig(host, port);
		} else {
			serverConfig = new TCPNetSyslogServerConfig(host, port);
		}
		serverConfig.setHost(host);
		serverConfig.setPort(port);
		serverConfig.setUseStructuredData(true);
		
		serverConfig.addEventHandler(this);
		return serverConfig;
	}
	
	private List<EventObserver> observers = new Vector<EventObserver>();
	@Override
	public void addEventObserver(EventObserver observer) {
		observers.add(observer);
	}

	@Override
	public void removeEventObserver(EventObserver observer) {
		observers.remove(observer);
	}

	@Override
	public void onEventRecived(Event e) {
		for (EventObserver o : observers) {
			o.eventReceived(e);
		}
	}

	@Override
	public void startup() throws Throwable {
		SyslogServer.createThreadedInstance(protocol.toLowerCase(), syslogConfig);
	}

	@Override
	public void shutdown() throws Throwable {
		SyslogServer.shutdown();
	}

	@Override
	public boolean isListening() {
		try {
			Thread syslogServerThread = SyslogServer.getThreadedInstance(protocol).getThread();
			return syslogServerThread.isAlive();
		} catch (Throwable e) {
			return false;
		}
	}

	@Override
	public String getName() {
		return "Syslog事件侦听器";
	}

	@Override
	public String getProtocol() {
		return protocol;
	}

	@Override
	public ListenerType getType() {
		return ListenerType.SYSLOG;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void event(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, SyslogServerEventIF event) {
		Date dt = event.getDate() == null ? new Date() : event.getDate();
		Severity st = Severity.getSeverity(event.getLevel());
		String ht = event.getHost();
		String msg = event.getMessage();
		Event e = new Event();
		e.setTimestamp(dt);
		e.setSeverity(st);
		e.setListenerType(ListenerType.SYSLOG);
		e.setSource(ht);
		e.setState(EventState.OPEN);
		e.setDigest(msg);
		Gson gson = new Gson();
		e.setData(gson.toJson(event));
		onEventRecived(e);
	}

	@Override
	public void destroy(SyslogServerIF arg0) {}

	@Override
	public void initialize(SyslogServerIF arg0) {}

	@Override
	public void exception(Object arg0, SyslogServerIF arg1, SocketAddress arg2, Exception arg3) {
	}

	@Override
	public void sessionClosed(Object arg0, SyslogServerIF arg1, SocketAddress arg2, boolean arg3) {}

	@Override
	public Object sessionOpened(SyslogServerIF arg0, SocketAddress arg1) {
		return null;
	}
}
