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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.littlestar.event_central.GlobalConnectionFactories;

public class BlackList {
	private final static Logger logger = Logger.getLogger(BlackList.class.getName());
	private final static Object lock = new Object();
	private static ConcurrentHashMap<String, ArrayList<String>> blacklist = new ConcurrentHashMap<String, ArrayList<String>>();

	public static void reload() throws Throwable {
		synchronized (lock) {
			blacklist.clear();
			final Connection connection = GlobalConnectionFactories.getConnection();
			String sqlText = "select * from lsec.blacklist";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sqlText);
			while (rs.next()) {
				String category = rs.getString("category");
				String keyword = rs.getString("keyword");
				ArrayList<String> keywords = blacklist.get(category);
				if (keywords == null) {
					keywords = new ArrayList<String>();
					blacklist.put(category, keywords);
				}
				keywords.add(keyword);
			}
			rs.close();
			stmt.close();
			connection.close();
		}
	}

	public static boolean check(String line, String category, boolean reload) {
		try {
			if (reload) {
				reload();
			}
			synchronized (lock) {
				ArrayList<String> keys = blacklist.get(category);
				return containKeys(line, keys);
			}
		} catch (Throwable e) {
			logger.log(Level.WARNING, "检查黑名单失败", e);
			return false;
		}
	}

	public static boolean check(String line, String category) {
		return check(line, category, true);
	}

	private static boolean containKeys(String line, ArrayList<String> keys) {
		if (line == null | keys == null)
			return false;
		for (String s : keys) {
			if (!isEmpty(s)) {
				if (line.contains(s.trim()))
					return true;
			}
		}
		return false;
	}

	private static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}
}
