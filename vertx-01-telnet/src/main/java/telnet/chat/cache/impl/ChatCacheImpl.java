package telnet.chat.cache.impl;

import telnet.chat.cache.ChatCache;
import telnet.chat.cache.bean.ChatRoomCacheBean;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author muyi
 * @description:
 * @date 2020-11-02 16:08:28
 */
public class ChatCacheImpl implements ChatCache {

    /**
     * 房间表
     */
    private ConcurrentHashMap<String, ChatRoomCacheBean> chatrooms;
    /**
     * 用户房间表
     */
    private ConcurrentHashMap<String, String> userChatroom;

    private volatile static ChatCache instance;

    private ChatCacheImpl(){
        chatrooms = new ConcurrentHashMap<String, ChatRoomCacheBean>();
        userChatroom = new ConcurrentHashMap<String, String>();
    }

    /**
     * @return ChatCache
     */
    public static ChatCache getInstance() {
        if (instance == null) {
            synchronized (ChatCacheImpl.class) {
                if (instance == null) {
                    instance = new ChatCacheImpl();
                }
            }
        }
        return instance;
    }

    /**
     * 通过userId获取房间
     *
     * @param userId
     * @return
     */
    @Override
    public ChatRoomCacheBean getChatRoomByUserId(String userId) {
        ChatRoomCacheBean chatRoomCacheBean;
        if (!userChatroom.containsKey(userId)) {
            chatRoomCacheBean = null;
        } else {
            String chatRoomName = getChatroomNameByUserId(userId);
            chatRoomCacheBean = chatrooms.get(chatRoomName);
        }
        return chatRoomCacheBean;
    }

    /**
     * 根据userId获取房间名
     *
     * @param userId
     * @return
     */
    @Override
    public String getChatroomNameByUserId(String userId) {
        return userChatroom.get(userId);
    }

    /**
     * 添加用户与房间的映射
     *
     * @param userId
     * @param chatroomName
     * @return
     */
    @Override
    public int addUserChatroom(String userId, String chatroomName) {
        int flag;
        if (userId == null || "".equals(userId)) {
            flag = -1;
        } else if (chatroomName == null || "".equals(chatroomName)) {
            flag = -1;
        } else {
            userChatroom.put(userId, chatroomName);
            flag = 1;
        }
        return flag;
    }

    /**
     * 添加房间
     *
     * @param chatRoomCacheBean
     * @return
     */
    @Override
    public int addChatroom(ChatRoomCacheBean chatRoomCacheBean) {
        int flag;
        if (chatRoomCacheBean == null) {
            flag = -1;
        } else if (chatRoomCacheBean.getChatRoomName() == null || "".equals(chatRoomCacheBean.getChatRoomName())) {
            flag = -1;
        } else {
            chatrooms.put(chatRoomCacheBean.getChatRoomName(), chatRoomCacheBean);
            flag = 1;
        }
        return flag;
    }

    /**
     * 通过userId删除用户房间的记录
     *
     * @param userId
     * @return
     */
    @Override
    public int delUserChatroomByUserId(String userId) {
        int flag;
        if (userId == null || "".equals(userId)) {
            flag = -1;
        } else {
            userChatroom.remove(userId);
            flag = 1;
        }
        return flag;
    }

    /**
     * 通过房间名删除房间
     * @param chatroomName
     * @return
     */
    @Override
    public int delChatroomByName(String chatroomName){
        int flag;
        if (chatroomName == null || "".equals(chatroomName)) {
            flag = -1;
        } else {
            chatrooms.remove(chatroomName);
            flag = 1;
        }
        return flag;
    }

    /**
     * 通过房间名获取房间
     * @param chatroomName
     * @return
     */
    @Override
    public ChatRoomCacheBean getChatroomByName(String chatroomName){
        return chatrooms.get(chatroomName);
    }
}
