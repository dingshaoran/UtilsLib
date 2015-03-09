package com.support.helper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * XmppConnection ������
 * 
 * @author Ф��SoAi
 * 
 */
public class AsmackHelper {
	private final int SERVER_PORT = 5222;
	private final String SERVER_HOST = "127.0.0.1";
	private XMPPConnection connection = null;
	private final String SERVER_NAME = "ubuntuserver4java";

	private static AsmackHelper asmackTools = new AsmackHelper();

	/**
	 * ����ģʽ
	 * 
	 * @return
	 */
	synchronized public static AsmackHelper getInstance() {
		return asmackTools;
	}

	/**
	 * ��������
	 */
	public XMPPConnection getConnection() {
		if (connection == null) {
			openConnection();
		}
		return connection;
	}

	/**
	 * ������
	 */
	public boolean openConnection() {
		try {
			if (null == connection || !connection.isAuthenticated()) {
				XMPPConnection.DEBUG_ENABLED = true;// ����DEBUGģʽ
				// ��������
				ConnectionConfiguration config = new ConnectionConfiguration(SERVER_HOST, SERVER_PORT, SERVER_NAME);
				config.setReconnectionAllowed(true);
				config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
				config.setSendPresence(true); // ״̬��Ϊ���ߣ�Ŀ��Ϊ��ȡ������Ϣ
				config.setSASLAuthenticationEnabled(false); // �Ƿ����ð�ȫ��֤
				config.setTruststorePath("/system/etc/security/cacerts.bks");
				config.setTruststorePassword("changeit");
				config.setTruststoreType("bks");
				connection = new XMPPConnection(config);
				connection.connect();// ���ӵ�������
				// ���ø���Provider����������ã�����޷���������
				configureConnection(ProviderManager.getInstance());
				return true;
			}
		} catch (XMPPException xe) {
			xe.printStackTrace();
			connection = null;
		}
		return false;
	}

	/**
	 * �ر�����
	 */
	public void closeConnection() {
		if (connection != null) {
			// �Ƴ��B�ӱO 
			// connection.removeConnectionListener(connectionListener);
			if (connection.isConnected())
				connection.disconnect();
			connection = null;
		}
		Log.i("XmppConnection", "�P�]�B��");
	}

	/**
	 * ��¼
	 * 
	 * @param account ��¼�ʺ�
	 * @param password ��¼����
	 * @return
	 */
	public boolean login(String account, String password) {
		try {
			if (getConnection() == null)
				return false;
			getConnection().login(account, password);
			// �����ھQ��B
			Presence presence = new Presence(Presence.Type.available);
			getConnection().sendPacket(presence);
			// ����B�ӱO 
			// --------����ȱ�ٵķ�������ʱע��
			// connectionListener = new TaxiConnectionListener();
			// getConnection().addConnectionListener(connectionListener);
			return true;
		} catch (XMPPException xe) {
			xe.printStackTrace();
		}
		return false;
	}

	/**
	 * ע��
	 * 
	 * @param account ע���ʺ�
	 * @param password ע������
	 * @return 1��ע��ɹ� 0��������û�з��ؽ��2������˺��Ѿ�����3��ע��ʧ��
	 */
	public String regist(String account, String password) {
		if (getConnection() == null)
			return "0";
		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(getConnection().getServiceName());
		// ע������createAccountע��ʱ��������UserName������jid����"@"ǰ��Ĳ��֡�
		reg.setUsername(account);
		reg.setPassword(password);
		// ���addAttribute����Ϊ�գ������������������־��android�ֻ������İɣ���������
		reg.addAttribute("android", "geolo_createUser_android");
		PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()), new PacketTypeFilter(IQ.class));
		PacketCollector collector = getConnection().createPacketCollector(filter);
		getConnection().sendPacket(reg);
		IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
		// Stop queuing resultsֹͣ����results���Ƿ�ɹ��Ľ����
		collector.cancel();
		if (result == null) {
			Log.e("regist", "No response from server.");
			return "0";
		} else if (result.getType() == IQ.Type.RESULT) {
			Log.v("regist", "regist success.");
			return "1";
		} else { // if (result.getType() == IQ.Type.ERROR)
			if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
				Log.e("regist", "IQ.Type.ERROR: " + result.getError().toString());
				return "2";
			} else {
				Log.e("regist", "IQ.Type.ERROR: " + result.getError().toString());
				return "3";
			}
		}
	}

	/**
	 * �����û�״̬
	 */
	public void setPresence(int code) {
		XMPPConnection con = getConnection();
		if (con == null)
			return;
		Presence presence;
		switch (code) {
		case 0:
			presence = new Presence(Presence.Type.available);
			con.sendPacket(presence);
			Log.v("state", "��������");
			break;
		case 1:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.chat);
			con.sendPacket(presence);
			Log.v("state", "����Q�Ұ�");
			break;
		case 2:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.dnd);
			con.sendPacket(presence);
			Log.v("state", "����æµ");
			break;
		case 3:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.away);
			con.sendPacket(presence);
			Log.v("state", "�����뿪");
			break;
		case 4:
			Roster roster = con.getRoster();
			Collection<RosterEntry> entries = roster.getEntries();
			for (RosterEntry entry : entries) {
				presence = new Presence(Presence.Type.unavailable);
				presence.setPacketID(Packet.ID_NOT_AVAILABLE);
				presence.setFrom(con.getUser());
				presence.setTo(entry.getUser());
				con.sendPacket(presence);
				Log.v("state", presence.toXML());
			}
			// ��ͬһ�û��������ͻ��˷�������״̬
			presence = new Presence(Presence.Type.unavailable);
			presence.setPacketID(Packet.ID_NOT_AVAILABLE);
			presence.setFrom(con.getUser());
			presence.setTo(StringUtils.parseBareAddress(con.getUser()));
			con.sendPacket(presence);
			Log.v("state", "��������");
			break;
		case 5:
			presence = new Presence(Presence.Type.unavailable);
			con.sendPacket(presence);
			Log.v("state", "��������");
			break;
		default:
			break;
		}
	}

	/**
	 * ��ȡ������
	 * 
	 * @return �����鼯��
	 */
	public List<RosterGroup> getGroups() {
		if (getConnection() == null)
			return null;
		List<RosterGroup> grouplist = new ArrayList<RosterGroup>();
		Collection<RosterGroup> rosterGroup = getConnection().getRoster().getGroups();
		Iterator<RosterGroup> i = rosterGroup.iterator();
		while (i.hasNext()) {
			grouplist.add(i.next());
		}
		return grouplist;
	}

	/**
	 * ��ȡĳ������������к���
	 * 
	 * @param roster
	 * @param groupName ����
	 * @return
	 */
	public List<RosterEntry> getEntriesByGroup(String groupName) {
		if (getConnection() == null)
			return null;
		List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();
		RosterGroup rosterGroup = getConnection().getRoster().getGroup(groupName);
		Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();
		Iterator<RosterEntry> i = rosterEntry.iterator();
		while (i.hasNext()) {
			Entrieslist.add(i.next());
		}
		return Entrieslist;
	}

	/**
	 * ��ȡ���к�����Ϣ
	 * 
	 * @return
	 */
	public List<RosterEntry> getAllEntries() {
		if (getConnection() == null)
			return null;
		List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();
		Collection<RosterEntry> rosterEntry = getConnection().getRoster().getEntries();
		Iterator<RosterEntry> i = rosterEntry.iterator();
		while (i.hasNext()) {
			Entrieslist.add(i.next());
		}
		return Entrieslist;
	}

	/**
	 * ��ȡ�û�VCard��Ϣ
	 * 
	 * @param connection
	 * @param user
	 * @return
	 * @throws XMPPException
	 */
	public VCard getUserVCard(String user) {
		if (getConnection() == null)
			return null;
		VCard vcard = new VCard();
		try {
			vcard.load(getConnection(), user);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return vcard;
	}

	/**
	 * ��ȡ�û�ͷ����Ϣ
	 * 
	 * @param connection
	 * @param user
	 * @return
	 */
	public Drawable getUserImage(String user) {
		if (getConnection() == null)
			return null;
		ByteArrayInputStream bais = null;
		try {
			VCard vcard = new VCard();
			// ���������룬���No VCard for
			ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new org.jivesoftware.smackx.provider.VCardProvider());
			if (user == "" || user == null || user.trim().length() <= 0) {
				return null;
			}
			vcard.load(getConnection(), user + "@" + getConnection().getServiceName());

			if (vcard == null || vcard.getAvatar() == null)
				return null;
			bais = new ByteArrayInputStream(vcard.getAvatar());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		// --------����ȱ�ٵķ�������ʱע��
		// return FormatTools.getInstance().InputStream2Drawable(bais);
		return null;
	}

	/**
	 * ���һ������
	 * 
	 * @param groupName
	 * @return
	 */
	public boolean addGroup(String groupName) {
		if (getConnection() == null)
			return false;
		try {
			getConnection().getRoster().createGroup(groupName);
			Log.v("addGroup", groupName + "�����ɹ�");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ɾ������
	 * 
	 * @param groupName
	 * @return
	 */
	public boolean removeGroup(String groupName) {
		return true;
	}

	/**
	 * ��Ӻ��� �޷���
	 * 
	 * @param userName
	 * @param name
	 * @return
	 */
	public boolean addUser(String userName, String name) {
		if (getConnection() == null)
			return false;
		try {
			getConnection().getRoster().createEntry(userName, name, null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ��Ӻ��� �з���
	 * 
	 * @param userName
	 * @param name
	 * @param groupName
	 * @return
	 */
	public boolean addUser(String userName, String name, String groupName) {
		if (getConnection() == null)
			return false;
		try {
			Presence subscription = new Presence(Presence.Type.subscribed);
			subscription.setTo(userName);
			userName += "@" + getConnection().getServiceName();
			getConnection().sendPacket(subscription);
			getConnection().getRoster().createEntry(userName, name, new String[] { groupName });
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ɾ������
	 * 
	 * @param userName
	 * @return
	 */
	public boolean removeUser(String userName) {
		if (getConnection() == null)
			return false;
		try {
			RosterEntry entry = null;
			if (userName.contains("@"))
				entry = getConnection().getRoster().getEntry(userName);
			else
				entry = getConnection().getRoster().getEntry(userName + "@" + getConnection().getServiceName());
			if (entry == null)
				entry = getConnection().getRoster().getEntry(userName);
			getConnection().getRoster().removeEntry(entry);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ��ѯ�û�
	 * 
	 * @param userName
	 * @return
	 * @throws XMPPException
	 */
	public List<HashMap<String, String>> searchUsers(String userName) {
		if (getConnection() == null)
			return null;
		HashMap<String, String> user = null;
		List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
		try {
			new ServiceDiscoveryManager(getConnection());

			UserSearchManager usm = new UserSearchManager(getConnection());

			Form searchForm = usm.getSearchForm(getConnection().getServiceName());
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("userAccount", true);
			answerForm.setAnswer("userPhote", userName);
			ReportedData data = usm.getSearchResults(answerForm, "search" + getConnection().getServiceName());

			Iterator<Row> it = data.getRows();
			Row row = null;
			while (it.hasNext()) {
				user = new HashMap<String, String>();
				row = it.next();
				user.put("userAccount", row.getValues("userAccount").next().toString());
				user.put("userPhote", row.getValues("userPhote").next().toString());
				results.add(user);
				// �����ڣ����з���,UserNameһ���ǿգ����������������裬һ���ǿ�
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * �޸�����
	 * 
	 * @param connection
	 * @param status
	 */
	public void changeStateMessage(String status) {
		if (getConnection() == null)
			return;
		Presence presence = new Presence(Presence.Type.available);
		presence.setStatus(status);
		getConnection().sendPacket(presence);
	}

	/**
	 * �޸��û�ͷ��
	 * 
	 * @param file
	 */
	public boolean changeImage(File file) {
		if (getConnection() == null)
			return false;
		try {
			VCard vcard = new VCard();
			vcard.load(getConnection());

			byte[] bytes;

			bytes = getFileBytes(file);
			String encodedImage = StringUtils.encodeBase64(bytes);
			vcard.setAvatar(bytes, encodedImage);
			vcard.setEncodedImage(encodedImage);
			vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>" + encodedImage + "</BINVAL>", true);

			ByteArrayInputStream bais = new ByteArrayInputStream(vcard.getAvatar());
			// --------����ȱ�ٵķ�������ʱע��
			// FormatTools.getInstance().InputStream2Bitmap(bais);

			vcard.save(getConnection());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * �ļ�ת�ֽ�
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private byte[] getFileBytes(File file) throws IOException {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			int bytes = (int) file.length();
			byte[] buffer = new byte[bytes];
			int readBytes = bis.read(buffer);
			if (readBytes != buffer.length) {
				throw new IOException("Entire file not read");
			}
			return buffer;
		} finally {
			if (bis != null) {
				bis.close();
			}
		}
	}

	/**
	 * ɾ����ǰ�û�
	 * 
	 * @return
	 */
	public boolean deleteAccount() {
		if (getConnection() == null)
			return false;
		try {
			getConnection().getAccountManager().deleteAccount();
			return true;
		} catch (XMPPException e) {
			return false;
		}
	}

	/**
	 * �޸�����
	 * 
	 * @return
	 */
	public boolean changePassword(String pwd) {
		if (getConnection() == null)
			return false;
		try {
			getConnection().getAccountManager().changePassword(pwd);
			return true;
		} catch (XMPPException e) {
			return false;
		}
	}

	/**
	 * ��ʼ���������б�
	 */
	public List<HostedRoom> getHostRooms() {
		if (getConnection() == null)
			return null;
		Collection<HostedRoom> hostrooms = null;
		List<HostedRoom> roominfos = new ArrayList<HostedRoom>();
		try {
			new ServiceDiscoveryManager(getConnection());
			hostrooms = MultiUserChat.getHostedRooms(getConnection(), getConnection().getServiceName());
			for (HostedRoom entry : hostrooms) {
				roominfos.add(entry);
				Log.i("room", "���֣�" + entry.getName() + " - ID:" + entry.getJid());
			}
			Log.i("room", "�����������:" + roominfos.size());
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return roominfos;
	}

	/**
	 * ��������
	 * 
	 * @param roomName ��������
	 */
	public MultiUserChat createRoom(String user, String roomName, String password) {
		if (getConnection() == null)
			return null;

		MultiUserChat muc = null;
		try {
			// ����һ��MultiUserChat
			muc = new MultiUserChat(getConnection(), roomName + "@conference." + getConnection().getServiceName());
			// ����������
			muc.create(roomName);
			// ��������ҵ����ñ�
			Form form = muc.getConfigurationForm();
			// ����ԭʼ������һ��Ҫ�ύ���±���
			Form submitForm = form.createAnswerForm();
			// ��Ҫ�ύ�ı����Ĭ�ϴ�
			for (Iterator<FormField> fields = form.getFields(); fields.hasNext();) {
				FormField field = fields.next();
				if (!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable() != null) {
					// ����Ĭ��ֵ��Ϊ��
					submitForm.setDefaultAnswer(field.getVariable());
				}
			}
			// ���������ҵ���ӵ����
			List<String> owners = new ArrayList<String>();
			owners.add(getConnection().getUser());// �û�JID
			submitForm.setAnswer("muc#roomconfig_roomowners", owners);
			// �����������ǳ־������ң�����Ҫ����������
			submitForm.setAnswer("muc#roomconfig_persistentroom", true);
			// ������Գ�Ա����
			submitForm.setAnswer("muc#roomconfig_membersonly", false);
			// ����ռ��������������
			submitForm.setAnswer("muc#roomconfig_allowinvites", true);
			if (!password.equals("")) {
				// �����Ƿ���Ҫ����
				submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
				// ���ý�������
				submitForm.setAnswer("muc#roomconfig_roomsecret", password);
			}
			// �ܹ�����ռ������ʵ JID �Ľ�ɫ
			// submitForm.setAnswer("muc#roomconfig_whois", "anyone");
			// ��¼����Ի�
			submitForm.setAnswer("muc#roomconfig_enablelogging", true);
			// ������ע����ǳƵ�¼
			submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
			// ����ʹ�����޸��ǳ�
			submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
			// �����û�ע�᷿��
			submitForm.setAnswer("x-muc#roomconfig_registration", false);
			// ��������ɵı�����Ĭ��ֵ����������������������
			muc.sendConfigurationForm(submitForm);
		} catch (XMPPException e) {
			e.printStackTrace();
			return null;
		}
		return muc;
	}

	/**
	 * ���������
	 * 
	 * @param user �ǳ�
	 * @param password ����������
	 * @param roomsName ��������
	 */
	public MultiUserChat joinMultiUserChat(String user, String roomsName, String password) {
		if (getConnection() == null)
			return null;
		try {
			// ʹ��XMPPConnection����һ��MultiUserChat����
			MultiUserChat muc = new MultiUserChat(getConnection(), roomsName + "@conference." + getConnection().getServiceName());
			// �����ҷ��񽫻����Ҫ���ܵ���ʷ��¼����
			DiscussionHistory history = new DiscussionHistory();
			history.setMaxChars(0);
			// history.setSince(new Date());
			// �û�����������
			muc.join(user, password, history, SmackConfiguration.getPacketReplyTimeout());
			Log.i("MultiUserChat", "�����ҡ�" + roomsName + "������ɹ�........");
			return muc;
		} catch (XMPPException e) {
			e.printStackTrace();
			Log.i("MultiUserChat", "�����ҡ�" + roomsName + "������ʧ��........");
			return null;
		}
	}

	/**
	 * ��ѯ�����ҳ�Ա����
	 * 
	 * @param muc
	 */
	public List<String> findMulitUser(MultiUserChat muc) {
		if (getConnection() == null)
			return null;
		List<String> listUser = new ArrayList<String>();
		Iterator<String> it = muc.getOccupants();
		// ��������������Ա����
		while (it.hasNext()) {
			// �����ҳ�Ա����
			String name = StringUtils.parseResource(it.next());
			listUser.add(name);
		}
		return listUser;
	}

	/**
	 * �����ļ�
	 * 
	 * @param user
	 * @param filePath
	 */
	public void sendFile(String user, String filePath) {
		if (getConnection() == null)
			return;
		// �����ļ����������
		FileTransferManager manager = new FileTransferManager(getConnection());

		// ����������ļ�����
		OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(user);

		// �����ļ�
		try {
			transfer.sendFile(new File(filePath), "You won't believe this!");
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ������Ϣ
	 * 
	 * @return
	 */
	public Map<String, List<HashMap<String, String>>> getHisMessage() {
		if (getConnection() == null)
			return null;
		Map<String, List<HashMap<String, String>>> offlineMsgs = null;

		try {
			OfflineMessageManager offlineManager = new OfflineMessageManager(getConnection());
			Iterator<Message> it = offlineManager.getMessages();

			int count = offlineManager.getMessageCount();
			if (count <= 0)
				return null;
			offlineMsgs = new HashMap<String, List<HashMap<String, String>>>();

			while (it.hasNext()) {
				Message message = it.next();
				String fromUser = StringUtils.parseName(message.getFrom());
				;
				HashMap<String, String> histrory = new HashMap<String, String>();
				histrory.put("useraccount", StringUtils.parseName(getConnection().getUser()));
				histrory.put("friendaccount", fromUser);
				histrory.put("info", message.getBody());
				histrory.put("type", "left");
				if (offlineMsgs.containsKey(fromUser)) {
					offlineMsgs.get(fromUser).add(histrory);
				} else {
					List<HashMap<String, String>> temp = new ArrayList<HashMap<String, String>>();
					temp.add(histrory);
					offlineMsgs.put(fromUser, temp);
				}
			}
			offlineManager.deleteMessages();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return offlineMsgs;
	}

	// /**
	// * �ж�OpenFire�û���״̬ strUrl :
	// * url��ʽ - http://my.openfire.com:9090/plugins/presence
	// * /status?jid=user1@SERVER_NAME&type=xml
	// * ����ֵ : 0 - �û�������; 1 - �û�����; 2 - �û�����
	// * ˵�� ������Ҫ�� OpenFire���� presence �����ͬʱ�����κ��˶����Է���
	// */
	// public int IsUserOnLine(String user) {
	// String url = "http://"+SERVER_HOST+":9090/plugins/presence/status?" +
	// "jid="+ user +"@"+ SERVER_NAME +"&type=xml";
	// int shOnLineState = 0; // ������
	// try {
	// URL oUrl = new URL(url);
	// URLConnection oConn = oUrl.openConnection();
	// if (oConn != null) {
	// BufferedReader oIn = new BufferedReader(new InputStreamReader(
	// oConn.getInputStream()));
	// if (null != oIn) {
	// String strFlag = oIn.readLine();
	// oIn.close();
	// System.out.println("strFlag"+strFlag);
	// if (strFlag.indexOf("type=\"unavailable\"") >= 0) {
	// shOnLineState = 2;
	// }
	// if (strFlag.indexOf("type=\"error\"") >= 0) {
	// shOnLineState = 0;
	// } else if (strFlag.indexOf("priority") >= 0
	// || strFlag.indexOf("id=\"") >= 0) {
	// shOnLineState = 1;
	// }
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return shOnLineState;
	// }

	/**
	 * ����providers�ĺ��� ASmack��/META-INFȱ��һ��smack.providers �ļ�
	 * 
	 * @param pm
	 */
	public void configureConnection(ProviderManager pm) {

		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());

		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
			Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
		}

		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster", new RosterExchangeProvider());

		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event", new MessageEventProvider());

		// Chat State
		pm.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());

		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider());

		// Service Discovery # Items
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());

		// Service Discovery # Info
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());

		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());

		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());

		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());

		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());

		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			// Not sure what's happening here.
		}

		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());

		// Offline Message Indicator
		pm.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());

		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());

		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());

		// FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());

		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());

		// Privacy
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
		pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());
	}

	/**
	 * �ж�OpenFire�û���״̬ strUrl : url��ʽ -
	 * http://my.openfire.com:9090/plugins/presence
	 * /status?jid=user1@SERVER_NAME&type=xml ����ֵ : 0 - �û�������; 1 - �û�����; 2 -
	 * �û����� ˵�� ������Ҫ�� OpenFire���� presence �����ͬʱ�����κ��˶����Է���
	 */
	public int IsUserOnLine(String user) {
		String url = "http://" + SERVER_HOST + ":9090/plugins/presence/status?" + "jid=" + user + "@" + SERVER_NAME + "&type=xml";
		int shOnLineState = 0; // ������
		try {
			URL oUrl = new URL(url);
			URLConnection oConn = oUrl.openConnection();
			if (oConn != null) {
				BufferedReader oIn = new BufferedReader(new InputStreamReader(oConn.getInputStream()));
				if (null != oIn) {
					String strFlag = oIn.readLine();
					oIn.close();
					System.out.println("strFlag" + strFlag);
					if (strFlag.indexOf("type=\"unavailable\"") >= 0) {
						shOnLineState = 2;
					}
					if (strFlag.indexOf("type=\"error\"") >= 0) {
						shOnLineState = 0;
					} else if (strFlag.indexOf("priority") >= 0 || strFlag.indexOf("id=\"") >= 0) {
						shOnLineState = 1;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shOnLineState;
	}

}
