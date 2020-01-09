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
import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;

import org.littlestar.event_central.utils.ConnectionFactory;

public class GlobalConnectionFactories {
	final static ConcurrentHashMap<String, ConnectionFactory> factories = new ConcurrentHashMap<String, ConnectionFactory>();
	public static final String DATASOURCE_XML_PATH = System.getProperty("user.dir") + File.separator + "datasource.xml";
	private static ConnectionFactory connectionFactory;
	private static final Object lock = new Object();
	
	public static Connection getConnection() throws Throwable {
		return getConnection(DATASOURCE_XML_PATH);
	}
	
	public static Connection getConnection(String cfg) throws Throwable {
		Connection connection;
		synchronized (lock) {
			ConnectionFactory factory = factories.get(cfg);
			if(factory == null) {
				connectionFactory = ConnectionFactory.newFactory(new File(cfg));
				factories.put(cfg, connectionFactory);
			}
			connection = connectionFactory.build();
		}
		return connection;
	}
}
