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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CloseEventServlet extends HttpServlet {
	private static final long serialVersionUID = -8229252003903624281L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		ServletContext context = getServletContext( );
		int eventId = -1;
		request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
		String pEventId = request.getParameter("eventId");
		
		PrintWriter out = response.getWriter();
		try {
			eventId = Integer.parseInt(pEventId);
		} catch (Throwable e) {
		}
		
		if (eventId >= 0) {
			try {
				String dsConfig = getServletContext().getInitParameter("ds_config");
				EventDAO eventDAO = new EventDAO(dsConfig);
				eventDAO.closeEvent(eventId);
				out.print("关闭事件[ " + eventId + " ]成功.");
			} catch (Throwable e) {
				String error = "关闭事件[ " + eventId + " ]失败!";
				context.log(error, e);
				out.print(error + e.getMessage());
			}
		}
		out.flush();
		out.close();
	}
}
