package telnet.finger.cache;

import telnet.finger.cache.bean.FingerGuessGroupCacheBean;
import telnet.finger.cache.impl.FingerCacheImpl;

/**
 * @author muyi
 * @description:
 * @date 2020-11-02 16:24:35
 */
public interface FingerCache {

    static FingerCache getInstance() {
        return FingerCacheImpl.getInstance();
    }
    /**
     * 添加房间游戏
     * @param chatroomUser
     * @param fingerGuessGroupCacheBean
     * @return
     */
    int addFingerGuessGroup(String chatroomUser, FingerGuessGroupCacheBean fingerGuessGroupCacheBean);

    /**
     * 根据房间名-用户名，获取猜拳游戏
     * @param chatroomUser
     * @return
     */
    FingerGuessGroupCacheBean getFingerGuessGroupByChatroomUser(String chatroomUser);

    /**
     * 模糊删除
     * @param chatroomName
     * @return
     */
    void delFingerGuessGroupByChatroomName(String chatroomName);

    /**
     * 模糊获取fingerGuessGroup
     * @param chatRoomName
     * @return
     */
    FingerGuessGroupCacheBean getOneFingerGuessGroup(String chatRoomName);

    /**
     * 通过key删除游戏
     * @param chatroomUser
     * @return
     */
    int delFingerGuessGroupByChatroomUser(String chatroomUser);
}
