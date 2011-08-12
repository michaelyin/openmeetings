package org.openmeetings.app.quartz.scheduler;


import org.openmeetings.app.data.calendar.management.AppointmentLogic;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.scheduling.IScheduledJob;
import org.red5.server.api.scheduling.ISchedulingService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class QuartzMeetingReminderJob implements IScheduledJob {

	private static Logger log = Red5LoggerFactory.getLogger(QuartzMeetingReminderJob.class, ScopeApplicationAdapter.webAppRootKey);
	@Autowired
	private AppointmentLogic appointmentLogic;
	
	public void execute(ISchedulingService service) {
		//log.debug("QuartzMeetingReminderJob.execute");
		try {
			appointmentLogic.doScheduledMeetingReminder();
		} catch (Exception err){
			log.error("execute",err);
		}
	}
}
