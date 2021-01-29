package telnet.chat.model.impl;

import telnet.chat.cache.ChatCache;
import telnet.chat.cache.bean.ChatRoomCacheBean;
import telnet.chat.model.ChatModel;

/**
 * @author muyi
 * @description:
 * @date 2020-11-02 16:09:32
 */
public class ChatModelImpl implements ChatModel {

    private ChatCache chatCache;

    private volatile static ChatModel instance;

    private ChatModelImpl() {
        chatCache = ChatCache.getInstance();
    }

    /**
     *
     * @return ChatModel
     */
    public static ChatModel getInstance() {
        if (instance == null) {
            synchronized (ChatModelImpl.class) {
                if (instance == null) {
                    instance = new ChatModelImpl();
                }
            }
        }
        return instance;
    }

    /**
     * 判断用户是否在房间中
     *
     * @param userId
     * @return
     */
    @Override
    public boolean isUserChating(String userId) {
        String chatRoomName = chatCache.getChatroomNameByUserId(userId);
        if (chatRoomName == null || "".equals(chatRoomName)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 对外接口：通过userId获取房间
     *
     * @param userId
     * @return
     */
    @Override
    public ChatRoomCacheBean getChatRoomByUserId(String userId) {
        return chatCache.getChatRoomByUserId(userId);
    }
}
