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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.littlestar.event_central.webui.HearbeatDAO.ListenerInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class QueryListenersServlet extends HttpServlet {
	private static final long serialVersionUID = 6859225629362783515L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		ServletContext context = getServletContext();
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-json");
		PrintWriter out = response.getWriter();
		List<ListenerInfo> listeners = null;
		String error = null;
		Map<String, Object> info = new HashMap<String, Object>();
		try {
			String dsConfig = getServletContext().getInitParameter("ds_config");
			HearbeatDAO eventDAO = new HearbeatDAO(dsConfig);
			listeners = eventDAO.getListeners();
		} catch (Throwable e) {
			error = "从数据库获监听列表失败!";
			context.log("error", e);
			error += e.getMessage();
			info.put("error", error);
		}
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		if (listeners != null)
			info.put("data", listeners);
		out.println(gson.toJson(info));
		out.flush();
		out.close();
	}

}
