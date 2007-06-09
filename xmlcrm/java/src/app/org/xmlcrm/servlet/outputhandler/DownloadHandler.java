package org.xmlcrm.servlet.outputhandler;

import http.utils.multipartrequest.MultipartRequest;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmlcrm.app.data.basic.Sessionmanagement;
import org.xmlcrm.app.data.user.Usermanagement;

public class DownloadHandler extends HttpServlet {

	private static final Log log = LogFactory.getLog(DownloadHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ServletException,
			IOException {

		try {
			String sid = httpServletRequest.getParameter("sid");
			if (sid == null) {
				sid = "default";
			}
			System.out.println("sid: " + sid);

			Long users_id = Sessionmanagement.getInstance().checkSession(sid);
			long User_LEVEL = Usermanagement.getInstance().getUserLevelByID(users_id);

			if (User_LEVEL > 0) {
				String room = httpServletRequest.getParameter("room");
				if(room == null){
					room = "default";
				}
				String domain = httpServletRequest.getParameter("domain");
				if(domain == null){
					domain = "default";
				}		
				
				String moduleName = httpServletRequest.getParameter("moduleName");
				if (moduleName == null) {
					moduleName = "nomodule";
				}				
				
				String parentPath = httpServletRequest.getParameter("parentPath");
				if (parentPath == null) {
					parentPath = "nomodule";
				}
				//make a complete name out of domain(organisation) + roomname
				String roomName = domain+"_"+room;
				//trim whitespaces cause it is a directory name
				roomName = StringUtils.deleteWhitespace(roomName);

				//Get the current User-Directory
				
				String current_dir = getServletContext().getRealPath("/");
				System.out.println("Current_dir: "+current_dir);

				String working_dir = "";

				System.out.println("#### moduleName: " + moduleName);

				working_dir = current_dir+File.separatorChar+"upload"+File.separatorChar;
				
				// Add the Folder for the Room

				if (moduleName.equals("videoconf1")) {
					if (parentPath.length() != 0) {
						
						working_dir = working_dir + roomName
								+ File.separatorChar + parentPath
								+ File.separatorChar;
						
					} else {
						working_dir = current_dir + roomName
								+ File.separatorChar;
					}
				} else {
					working_dir = working_dir + roomName + File.separatorChar;
				}

				if (!moduleName.equals("nomodule")) {

					String requestedFile = httpServletRequest.getParameter("fileName");
					System.out.println("requestedFile: " + requestedFile);

					String full_path = working_dir + requestedFile;
					RandomAccessFile rf = new RandomAccessFile(full_path, "r");

					httpServletResponse.reset();
					httpServletResponse.resetBuffer();
					OutputStream out = httpServletResponse.getOutputStream();
					httpServletResponse.setContentType("APPLICATION/OCTET-STREAM");
					httpServletResponse.setHeader("Content-Disposition","attachment; filename=\"" + requestedFile + "\"");
					httpServletResponse.setHeader("Content-Length", ""+ rf.length());

					byte[] buffer = new byte[1024];
					int readed = -1;

					while ((readed = rf.read(buffer, 0, buffer.length)) > -1) {
						out.write(buffer, 0, readed);
					}

					rf.close();

					out.flush();
					out.close();

				}
			}

		} catch (Exception er) {
			System.out.println("Error downloading: " + er);
		}
	}

}
