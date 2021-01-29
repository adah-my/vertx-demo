package telnet.chat.cache;

import telnet.chat.cache.bean.ChatRoomCacheBean;
import telnet.chat.cache.impl.ChatCacheImpl;

/**
 * @author muyi
 * @description:
 * @date 2020-11-02 16:38:14
 */
public interface ChatCache {

    static ChatCache getInstance() {
        return ChatCacheImpl.getInstance();
    }

    /**
     * 通过userId获取房间
     *
     * @param userId
     * @return
     */
    ChatRoomCacheBean getChatRoomByUserId(String userId);

    /**
     * 根据userId获取房间名
     *
     * @param userId
     * @return
     */
    String getChatroomNameByUserId(String userId);

    /**
     * 添加用户与房间的映射
     *
     * @param userId
     * @param chatroomName
     * @return
     */
    int addUserChatroom(String userId, String chatroomName);

    /**
     * 添加房间
     *
     * @param chatRoomCacheBean
     * @return
     */
    int addChatroom(ChatRoomCacheBean chatRoomCacheBean);

    /**
     * 通过userId删除用户房间的记录
     *
     * @param userId
     * @return
     */
    int delUserChatroomByUserId(String userId);

    /**
     * 通过房间名删除房间
     * @param chatroomName
     * @return
     */
    int delChatroomByName(String chatroomName);

    /**
     * 通过房间名获取房间
     * @param chatroomName
     * @return
     */
    ChatRoomCacheBean getChatroomByName(String chatroomName);
}
