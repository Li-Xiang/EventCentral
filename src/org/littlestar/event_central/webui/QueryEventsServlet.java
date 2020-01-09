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
import org.littlestar.event_central.Event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class QueryEventsServlet extends HttpServlet {
	private static final long serialVersionUID = 4123608168581903367L;
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		int draw = 0, start = 0, length = 10;
		boolean openOnly = true;
		request.setCharacterEncoding("UTF-8");
		String pDraw = request.getParameter("draw");
		try {
			draw = Integer.parseInt(pDraw);
		} catch (Throwable e) {
		}

		String pStart = request.getParameter("start");
		try {
			start = Integer.parseInt(pStart);
		} catch (Throwable e) {
		}

		String pLength = request.getParameter("length");
		try {
			length = Integer.parseInt(pLength);
		} catch (Throwable e) {
		}
		
		String pShowAll = request.getParameter("showAll");
		openOnly = !Boolean.parseBoolean(pShowAll);
		
		ServletContext context = getServletContext();
		/*
		Enumeration<String> keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			 String key = keys.nextElement();
			 String value = request.getParameter(key);
			 context.log(key+" = "+value);
		}
        */
        response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-json");
		PrintWriter out = response.getWriter();
		List<Event> events = null;
		String error = null;
		int iTotalRecords = 0;
		//int iTotalDisplayRecords = 0;
		Map<String, Object> info = new HashMap<String, Object>();
		try {
			String dsConfig=getServletContext().getInitParameter("ds_config");
			EventDAO eventDAO = new EventDAO(dsConfig);
			iTotalRecords = eventDAO.getEventCount(openOnly);
			events = eventDAO.getEvents(start, length, openOnly);
		} catch (Throwable e) {
			error = "从数据库获事件列表失败!";
			context.log("error", e);
			info.put("error", error += e.getMessage());
		}
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		
		info.put("iTotalRecords", iTotalRecords);        // recordsTotal
		info.put("iTotalDisplayRecords", iTotalRecords); // recordsFiltered
		info.put("draw", draw);
		if (events != null)
			info.put("data", events);
		out.println(gson.toJson(info));
		out.flush();
		out.close();
	}
}
