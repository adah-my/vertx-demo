package telnet.chat.model;

import telnet.chat.cache.bean.ChatRoomCacheBean;
import telnet.chat.model.impl.ChatModelImpl;

/**
 * @author muyi
 * @description:
 * @date 2020-11-02 16:36:27
 */
public interface ChatModel {
    static ChatModel getInstance() {
        return ChatModelImpl.getInstance();
    }
    /**
     * 判断用户是否在房间中
     *
     * @param userId
     * @return
     */
    boolean isUserChating(String userId);

    /**
     * 对外接口：通过userId获取房间
     *
     * @param userId
     * @return
     */
    ChatRoomCacheBean getChatRoomByUserId(String userId);
}
