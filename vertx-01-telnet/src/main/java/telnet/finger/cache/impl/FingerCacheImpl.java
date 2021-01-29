package telnet.finger.cache.impl;

import telnet.finger.cache.FingerCache;
import telnet.finger.cache.bean.FingerGuessGroupCacheBean;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author muyi
 * @description:
 * @date 2020-11-02 16:10:27
 */
public class FingerCacheImpl implements FingerCache {

    /**
     * 房间名-用户名：猜拳游戏组
     */
    private ConcurrentHashMap<String, FingerGuessGroupCacheBean> fingerGuessGroups;


    private volatile static FingerCache instance;

    private FingerCacheImpl(){
        fingerGuessGroups = new ConcurrentHashMap<String, FingerGuessGroupCacheBean>();
    }

    /**
     * @return FingerCache
     */
    public static FingerCache getInstance() {
        if (instance == null) {
            synchronized (FingerCacheImpl.class) {
                if (instance == null) {
                    instance = new FingerCacheImpl();
                }
            }
        }
        return instance;
    }

    /**
     * 添加房间游戏
     * @param chatroomUser
     * @param fingerGuessGroupCacheBean
     * @return
     */
    @Override
    public int addFingerGuessGroup(String chatroomUser, FingerGuessGroupCacheBean fingerGuessGroupCacheBean){
        int flag;
        if (chatroomUser == null || "".equals(chatroomUser)){
            flag = -1;
        }else if (fingerGuessGroupCacheBean == null){
            flag = -1;
        }else {
            fingerGuessGroups.put(chatroomUser, fingerGuessGroupCacheBean);
            flag = 1;
        }
        return flag;
    }

    /**
     * 根据房间名-用户名，获取猜拳游戏
     * @param chatroomUser
     * @return
     */
    @Override
    public FingerGuessGroupCacheBean getFingerGuessGroupByChatroomUser(String chatroomUser){
        if (chatroomUser == null || "".equals(chatroomUser)){
            return null;
        }else{
            return fingerGuessGroups.get(chatroomUser);
        }
    }

    /**
     * 模糊删除
     * @param chatroomName
     * @return
     */
    @Override
    public void delFingerGuessGroupByChatroomName(String chatroomName){
        if (chatroomName == null || "".equals(chatroomName)){
        } else if (fingerGuessGroups.size() == 0){
        }else {

            Iterator<Map.Entry<String, FingerGuessGroupCacheBean>> iterator = fingerGuessGroups.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, FingerGuessGroupCacheBean> entry = iterator.next();
                String[] chatroomUser = entry.getKey().split("-finger-");
                if (chatroomUser[0].equals(chatroomName)){
                    iterator.remove();
                }else {
                    continue;
                }
            }
        }
    }

    /**
     * 模糊获取fingerGuessGroup
     * @param chatRoomName
     * @return
     */
    @Override
    public FingerGuessGroupCacheBean getOneFingerGuessGroup(String chatRoomName) {
        Iterator<Map.Entry<String, FingerGuessGroupCacheBean>> iterator = fingerGuessGroups.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, FingerGuessGroupCacheBean> entry = iterator.next();
            String[] chatroomUser = entry.getKey().split("-finger-");
            if (chatroomUser[0].equals(chatRoomName)){
                return entry.getValue();
            }else {
                continue;
            }
        }

        return null;
    }

    /**
     * 通过key删除游戏
     * @param chatroomUser
     * @return
     */
    @Override
    public int delFingerGuessGroupByChatroomUser(String chatroomUser) {
        int flag;
        if (chatroomUser == null || "".equals(chatroomUser)){
            flag = -1;
        }else if (fingerGuessGroups.size() == 0){
            flag = -1;
        }else if (!fingerGuessGroups.containsKey(chatroomUser)){
            flag = -1;
        }else {
            fingerGuessGroups.remove(chatroomUser);
            flag = 1;
        }
        return flag;
    }
}
