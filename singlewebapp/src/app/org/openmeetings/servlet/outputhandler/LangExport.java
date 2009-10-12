package org.openmeetings.servlet.outputhandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.io.XMLWriter;
import org.dom4j.io.OutputFormat;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.basic.FieldLanguageDaoImpl;
import org.openmeetings.app.hibernate.beans.lang.FieldLanguage;
import org.openmeetings.app.hibernate.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.hibernate.beans.lang.Fieldvalues;
import org.openmeetings.app.data.basic.Fieldmanagment;

/**
 * 
 * @author sebastianwagner
 *
 */
public class LangExport extends HttpServlet {

	private static final Logger log = Logger.getLogger(LangExport.class);

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
			log.debug("sid: " + sid);
			
			String language = httpServletRequest.getParameter("language");
			if (language == null) {
				language = "0";
			}
			Long language_id = Long.valueOf(language).longValue();
			log.debug("language_id: " + language_id);

			Long users_id = Sessionmanagement.getInstance().checkSession(sid);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);

			log.debug("users_id: "+users_id);
			log.debug("user_level: "+user_level);
			
			if (user_level!=null && user_level > 0) {
				FieldLanguage fl = FieldLanguageDaoImpl.getInstance().getFieldLanguageById(language_id);

				List<Fieldvalues> fvList = Fieldmanagment.getInstance().getMixedFieldValuesList(language_id);
				
				if (fl!=null && fvList!=null) {
					Document doc = this.createDocument(fvList);
					
					String requestedFile = fl.getName()+".xml";
					
					httpServletResponse.reset();
					httpServletResponse.resetBuffer();
					OutputStream out = httpServletResponse.getOutputStream();
					httpServletResponse.setContentType("APPLICATION/OCTET-STREAM");
					httpServletResponse.setHeader("Content-Disposition","attachment; filename=\"" + requestedFile + "\"");
					//httpServletResponse.setHeader("Content-Length", ""+ rf.length());
					
					this.serializetoXML(out, "UTF-8", doc);
					
					out.flush();
					out.close();
				}
			} else {
				log.debug("ERROR LangExport: not authorized FileDownload "+(new Date()));
			}
	
		} catch (Exception er) {
			log.error("ERROR ", er);
			System.out.println("Error exporting: " + er);
			er.printStackTrace();
		}
	}

	public Document createDocument(List<Fieldvalues> fvList) throws Exception {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("UTF-8");
		document.addComment(
				"###############################################\n" +
				"This File is auto-generated by the LanguageEditor \n" +
				"to add new Languages or modify/customize it use the LanguageEditor \n" +
				"see http://code.google.com/p/openmeetings/wiki/LanguageEditor for Details \n" +
				"###############################################");
		
		Element root = document.addElement("language");

		for (Iterator<Fieldvalues> it = fvList.iterator();it.hasNext();) {
			Fieldvalues fv = it.next();
			Element eTemp = root.addElement("string")
					.addAttribute("id", fv.getFieldvalues_id().toString())
					.addAttribute("name", fv.getName());
			Element value = eTemp.addElement("value");
			if (fv.getFieldlanguagesvalue()!=null) {
				value.addText(fv.getFieldlanguagesvalue().getValue());
			} else {
				//Add english default text
				Fieldlanguagesvalues flv = Fieldmanagment.getInstance().getFieldByIdAndLanguage(fv.getFieldvalues_id(), 1L);
				if (flv != null) {
					value.addText(flv.getValue());
				} else {
					value.addText("");
				}
			}
		}

		return document;
	}	
	public void serializetoXML(OutputStream out, String aEncodingScheme, Document doc)
			throws Exception {
		OutputFormat outformat = OutputFormat.createPrettyPrint();
		outformat.setEncoding(aEncodingScheme);
		XMLWriter writer = new XMLWriter(out, outformat);
		writer.write(doc);
		writer.flush();
	}


}
