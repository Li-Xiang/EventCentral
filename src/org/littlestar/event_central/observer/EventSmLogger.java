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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.littlestar.event_central.Event;
import org.littlestar.event_central.EventObserver;
import org.littlestar.event_central.Server;
import org.littlestar.event_central.utils.BlackList;

public class EventSmLogger implements EventObserver {
	private final static Logger logger = Logger.getLogger(Server.class.getName());
	private final SmWriter smWriter;
	public EventSmLogger() {
		smWriter = new SmWriter();
		smWriter.start();
	}
	@Override
	public void eventReceived(Event event) {
		smWriter.put(event);
	}
	
	@Override
	public void close() {
	}

	public void setSmAddress(String addr) {
		smWriter.setSmAddress(addr);
	}
	
	public void setSmPort(int port) {
		smWriter.setSmPort(port);
	}
	
	public void setPhoneNumbers(String phoneNumbers) {
		smWriter.setPhoneNumbers(phoneNumbers);
	}
	
	public void setCharset(String charsetName) {
		smWriter.setCharset(charsetName);
	}
	
	private static class SmWriter extends Thread {
		private String addr = "192.168.220.221";
		private int port = 7070;
		private String phoneNumbers = "";
		private String charsetName = "GBK";
		private volatile boolean running = true;
		private final BlockingQueue<Event> writeQueue = new LinkedBlockingQueue<Event>(500);
		public void put(Event event) {
			try {
				writeQueue.put(event);
			} catch (Throwable e) {
				logger.log(Level.WARNING, "事件加入短信写队列失败!", e);
			}
		}
		
		public void setSmAddress(String addr) {
			this.addr = addr;
		}
		
		public void setSmPort(int port) {
			this.port = port;
		}
		
		public void setPhoneNumbers(String phoneNumbers) {
			this.phoneNumbers = phoneNumbers;
		}
		
		public void setCharset(String charsetName) {
			this.charsetName = charsetName;
		}
		
		@Override
		public void run() {
			while (running) {
				try {
					Event event = writeQueue.take();
					write(event);
				} catch (Throwable e) {
					logger.log(Level.WARNING, "事件发送短信通知失败!",  e);
				}
			}
		}
		
		private int write(Event event) throws Throwable {
			String digest = event.getDigest();
			if (BlackList.check(digest, "digest")) {
				logger.info("事件摘要\""+digest+"\"包含黑名单关键字,跳过短信通知. ");
				return 0;
			}
			StringBuilder data = new StringBuilder();
			data.append(phoneNumbers.trim()).append("||").append(event.getSource()).append(": ").append(digest).append("|MSGEOF");
			String text = data.toString();
			DatagramSocket datagramSocket = null;
			try {
				datagramSocket = new DatagramSocket();
				byte[] sendData = text.getBytes(charsetName);
				InetAddress address = InetAddress.getByName(addr);
				DatagramPacket packet = new DatagramPacket(sendData, sendData.length, address, port);
				datagramSocket.send(packet);
			} catch (Throwable e) {
				throw e;
			} finally {
				if (datagramSocket != null)
					datagramSocket.close();
			}
			return 1;
		}
	}
}
