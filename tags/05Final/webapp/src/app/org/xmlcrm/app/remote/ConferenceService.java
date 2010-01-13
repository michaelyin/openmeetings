package org.xmlcrm.app.remote;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmlcrm.app.data.basic.Sessionmanagement;
import org.xmlcrm.app.data.beans.basic.SearchResult;
import org.xmlcrm.app.data.user.Usermanagement;
import org.xmlcrm.app.data.conference.Roommanagement;
import org.xmlcrm.app.hibernate.beans.rooms.Rooms;
import org.xmlcrm.app.hibernate.beans.rooms.Rooms_Organisation;
import org.xmlcrm.app.conference.videobeans.RoomClient;

/**
 * 
 * @author swagner
 *
 */
public class ConferenceService {
	
	private static final Log log = LogFactory.getLog(ConferenceService.class);
	
	/**
	 * get a List of all availible Rooms of this organisation
	 * @param SID
	 * @param organisation_id
	 * @return
	 */
	public List<Rooms_Organisation> getRoomsByOrganisationAndType(String SID, long organisation_id, long roomtypes_id){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long User_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        List<Rooms_Organisation> roomOrgsList = Roommanagement.getInstance().getRoomsOrganisationByOrganisationIdAndRoomType(User_level, organisation_id, roomtypes_id);
        for (Iterator<Rooms_Organisation> iter = roomOrgsList.iterator();iter.hasNext();) {
        	Rooms_Organisation orgRoom = iter.next();
        	orgRoom.getRoom().setCurrentusers(this.getRoomClientsListByRoomId(orgRoom.getRoom().getRooms_id()));
        }
        return roomOrgsList;        
	}
	
	/**
	 * gets all rooms of an organisation
	 * TODO:check if the requesting user is also member of that organisation
	 * @param SID
	 * @param organisation_id
	 * @return
	 */
	public SearchResult getRoomsByOrganisation(String SID, long organisation_id, int start, int max, String orderby, boolean asc){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long User_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        return Roommanagement.getInstance().getRoomsOrganisationByOrganisationId(User_level, organisation_id, start, max, orderby, asc);
	}	
	
	/**
	 * get a List of all public availible rooms
	 * @param SID
	 * @param organisation_id
	 * @return
	 */
	public List getRoomsPublic(String SID, Long roomtypes_id){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long User_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        log.error("getRoomsPublic user_level: "+User_level);
        List<Rooms> roomList = Roommanagement.getInstance().getPublicRooms(User_level, roomtypes_id);
        for (Iterator<Rooms> iter = roomList.iterator();iter.hasNext();) {
        	Rooms rooms = iter.next();
        	rooms.setCurrentusers(this.getRoomClientsListByRoomId(rooms.getRooms_id()));
        }
        return roomList;
	}
	
	/**
	 * 
	 * @param SID
	 * @return
	 */
	public List getRoomTypes(String SID){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        return Roommanagement.getInstance().getAllRoomTypes(user_level);
	}
	
	/**
	 * 
	 * @param SID
	 * @param rooms_id
	 * @return
	 */
	public Rooms getRoomById(String SID, long rooms_id){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        return Roommanagement.getInstance().getRoomById(user_level,rooms_id);
	}
	
	/**
	 * gets a list of all availible rooms
	 * @param SID
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
	public SearchResult getRooms(String SID, int start, int max, String orderby, boolean asc){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        return Roommanagement.getInstance().getRooms(user_level, start, max, orderby, asc);
	}
	
	/**
	 * get all Organisations of a room
	 * @param SID
	 * @param rooms_id
	 * @return
	 */
	public List getOrganisationByRoom(String SID,long rooms_id){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        return Roommanagement.getInstance().getOrganisationsByRoom(user_level, rooms_id);
	}

	
	/**
	 * 
	 * @param SID
	 * @param name
	 * @param roomtypes_id
	 * @deprecated
	 * @return
	 */
	public Long addRoomPublic(String SID, String name, long roomtypes_id){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long User_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        return Roommanagement.getInstance().addRoom(User_level, name, roomtypes_id,"", new Long(4), true,null,
				290, 280, 2, 2,
				400,
				true, 296, 2, 592, 660,
				true, 2, 284, 310, 290);
	}
	
	/**
	 * 
	 * @param SID
	 * @param organisation_id
	 * @param name
	 * @param roomtypes_id
	 * @param ispublic
	 * @deprecated
	 * @return
	 */
	public Long addRoomOrganisation(String SID, long organisation_id, String name, long roomtypes_id, boolean ispublic){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        long User_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        Long rooms_id = Roommanagement.getInstance().addRoom(User_level, name, roomtypes_id,"", new Long(4), ispublic, null,
				290, 280, 2, 2,
				400,
				true, 296, 2, 592, 660,
				true, 2, 284, 310, 290);
        return Roommanagement.getInstance().addRoomToOrganisation(User_level, rooms_id, organisation_id);
	}
	
	/**
	 * 
	 * @param SID
	 * @param argObject
	 * @return
	 */
	public Long saveOrUpdateRoom(String SID, Object argObject){
		try {
	        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        long User_level = Usermanagement.getInstance().getUserLevelByID(users_id);
	        LinkedHashMap argObjectMap = (LinkedHashMap) argObject;
	        LinkedHashMap organisations = (LinkedHashMap) argObjectMap.get("organisations");
	        Long rooms_id = Long.valueOf(argObjectMap.get("rooms_id").toString()).longValue();
	        log.error("rooms_id "+rooms_id);
	        if (rooms_id==0){
	        	return Roommanagement.getInstance().addRoom(User_level, argObjectMap.get("name").toString(), 
	        			Long.valueOf(argObjectMap.get("roomtypes_id").toString()).longValue(),
	        			argObjectMap.get("comment").toString(), Long.valueOf(argObjectMap.get("numberOfPartizipants").toString()).longValue(),
	        			Boolean.valueOf(argObjectMap.get("ispublic").toString()),organisations,
	        			Integer.valueOf(argObjectMap.get("videoPodWidth").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("videoPodHeight").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("videoPodXPosition").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("videoPodYPosition").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("moderationPanelXPosition").toString()).intValue(),
	        			Boolean.valueOf(argObjectMap.get("showWhiteBoard").toString()).booleanValue(),
	        			Integer.valueOf(argObjectMap.get("whiteBoardPanelXPosition").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("whiteBoardPanelYPosition").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("whiteBoardPanelHeight").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("whiteBoardPanelWidth").toString()).intValue(),
	        			Boolean.valueOf(argObjectMap.get("showFilesPanel").toString()).booleanValue(),
	        			Integer.valueOf(argObjectMap.get("filesPanelXPosition").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("filesPanelYPosition").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("filesPanelHeight").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("filesPanelWidth").toString()).intValue());
	        } else if (rooms_id>0){
	        	return Roommanagement.getInstance().updateRoom(User_level, rooms_id, 
	        			Long.valueOf(argObjectMap.get("roomtypes_id").toString()).longValue(), 
	        			argObjectMap.get("name").toString(), Boolean.valueOf(argObjectMap.get("ispublic").toString()),
	        			argObjectMap.get("comment").toString(),
	        			Long.valueOf(argObjectMap.get("numberOfPartizipants").toString()).longValue(),organisations,
	        			Integer.valueOf(argObjectMap.get("videoPodWidth").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("videoPodHeight").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("videoPodXPosition").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("videoPodYPosition").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("moderationPanelXPosition").toString()).intValue(),
	        			Boolean.valueOf(argObjectMap.get("showWhiteBoard").toString()).booleanValue(),
	        			Integer.valueOf(argObjectMap.get("whiteBoardPanelXPosition").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("whiteBoardPanelYPosition").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("whiteBoardPanelHeight").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("whiteBoardPanelWidth").toString()).intValue(),
	        			Boolean.valueOf(argObjectMap.get("showFilesPanel").toString()).booleanValue(),
	        			Integer.valueOf(argObjectMap.get("filesPanelXPosition").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("filesPanelYPosition").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("filesPanelHeight").toString()).intValue(),
	        			Integer.valueOf(argObjectMap.get("filesPanelWidth").toString()).intValue());
	        }
		} catch (Exception e){
			log.error("saveOrUpdateRoom",e);
		}
		return null;
	}
	
	/**
	 * 
	 * @param SID
	 * @param rooms_id
	 * @return
	 */
	public Long deleteRoom(String SID, long rooms_id){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        return Roommanagement.getInstance().deleteRoomById(user_level, rooms_id);
	}
	
	/**
	 * return all participants of a room
	 * @param room_id
	 * @return
	 */
	public List<RoomClient> getRoomClientsListByRoomId(Long room_id) {
		try {
			//log.error("getRoomClientsListByRoomId: "+room_id);
			LinkedList<RoomClient> clients = new LinkedList<RoomClient>();
			HashMap<String,RoomClient> clientList = Application.getClientList();
			for (Iterator<String> iter = clientList.keySet().iterator();iter.hasNext();) {
				RoomClient rcl = clientList.get(iter.next());
				//log.error("COMPARE: "+rcl.getRoom_id()+" || "+room_id);
				if (rcl.getRoom_id()!=null && rcl.getRoom_id().equals(room_id)) clients.add(rcl);
			}
			return clients;
		} catch (Exception err) {
			log.error("[getRoomClientsListByRoomId]",err);
		}
		return null;
	}
	
}