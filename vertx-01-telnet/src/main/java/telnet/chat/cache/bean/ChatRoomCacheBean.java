package telnet.chat.cache.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author muyi
 * @description: 房间
 * @date 2020-10-28 11:57:17
 */
public class ChatRoomCacheBean {

    /**
     * 房间名字
     */
    private String chatRoomName;
    /**
     * 房间所有的用户
     */
    private List<String> chatUsers;

    public ChatRoomCacheBean(String chatRoomName) {
        this.chatRoomName = chatRoomName;
        chatUsers = new ArrayList<>();
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public List<String> getChatUsers() {
        return chatUsers;
    }

    public void setChatUsers(List<String> chatUsers) {
        this.chatUsers = chatUsers;
    }


    @Override
    public String toString() {
        return "ChatRoom{" +
                "chatRoomName='" + chatRoomName + '\'' +
                ", chatUsers=" + chatUsers +
                '}';
    }
}
