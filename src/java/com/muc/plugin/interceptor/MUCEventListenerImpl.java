package com.muc.plugin.interceptor;

import org.dom4j.Element;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.muc.MUCEventListener;
import org.jivesoftware.openfire.muc.MUCRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

import com.muc.plugin.dao.MUCDao;

/**
 * muc房间的监听类
 * @author www.mesoftware.cn
 *
 */
public class MUCEventListenerImpl implements MUCEventListener {

	private static final Logger log = LoggerFactory.getLogger(MUCEventListenerImpl.class);
	
	
	@Override
	public void roomCreated(JID roomJID) {
		log.warn("roomCreated:"+roomJID);
	}

	@Override
	public void roomDestroyed(JID roomJID) {
		log.warn("roomDestroyed:"+roomJID);
	}

	
	@Override
	public void occupantJoined(JID roomJID, JID user, String nickname) {
		String mjid = user.toBareJID();
		if(MUCDao.exists(mjid, roomJID.getNode()))  //如果成员存在
			return ;
		else if( !MUCDao.existsMember(roomJID.getNode()) ){ //如果muc房间里面一个用户都还不存在
			return ;
		}
		
		log.warn("occupantJoined:"+roomJID+">"+user+":"+nickname);
		MUCRoom mucroom =XMPPServer.getInstance().getMultiUserChatManager().getMultiUserChatService(roomJID).getChatRoom(roomJID.getNode());
		IQ iq = new IQ(IQ.Type.set); 
		Element frag = iq.setChildElement("query", "http://jabber.org/protocol/muc#member");
        Element item = frag.addElement("item");
        item.addAttribute("affiliation", "member");
        item.addAttribute("jid", mjid);
		item.addAttribute("nick",nickname);
        // Send the IQ packet that will modify the room's configuration
        try {
			mucroom.getIQAdminHandler().handleIQ(iq, mucroom.getRole());
			if(nickname != null  && !"".equals(nickname))
			   MUCDao.updateNick(mucroom.getID(), mjid, nickname);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

	@Override
	public void occupantLeft(JID roomJID, JID user) {
		System.out.println(user);
	}

	@Override
	public void nicknameChanged(JID roomJID, JID user, String oldNickname,
			String newNickname) {
		log.warn("nicknameChanged:"+user);
	}

	@Override
	public void messageReceived(JID roomJID, JID user, String nickname,
			Message message) {
		log.warn("nicknameChanged:"+user);
	}

	@Override
	public void privateMessageRecieved(JID toJID, JID fromJID, Message message) {
		log.warn("privateMessageRecieved:"+toJID);
	}

	@Override
	public void roomSubjectChanged(JID roomJID, JID user, String newSubject) {
		log.warn("roomSubjectChanged:"+user);
	}


}
