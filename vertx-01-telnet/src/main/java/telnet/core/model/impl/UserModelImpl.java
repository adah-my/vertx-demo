package telnet.core.model.impl;

import io.vertx.core.net.NetSocket;
import telnet.core.model.UserModel;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author muyi
 * @description:
 * @date 2020-11-02 16:53:15
 */
public class UserModelImpl implements UserModel {


    private volatile static UserModel instance;

    private ConcurrentHashMap<String, String> userHandlerMap;
    private ConcurrentHashMap<String, NetSocket> userSocketMap;

    private UserModelImpl() {
        userHandlerMap = new ConcurrentHashMap<>();
        userSocketMap = new ConcurrentHashMap<>();
    }

    /**
     * @return UserModel
     */
    public static UserModel getInstance() {
        if (instance == null) {
            synchronized (UserModelImpl.class) {
                if (instance == null) {
                    instance = new UserModelImpl();
                }
            }
        }
        return instance;
    }

    /**
     * 测试
     *
     * @return
     */
    @Override
    public String toString() {
        return "UserModel{" +
                "userHandlerMap=" + userHandlerMap +
                ", userSocketMap=" + userSocketMap +
                '}';
    }

    /**
     * 删除userId
     *
     * @param userId
     */
    @Override
    public void removeUserId(String userId) {
        userHandlerMap.remove(userId);
        userSocketMap.remove(userId);
    }

    /**
     * 添加UserId
     *
     * @param userId
     * @param socket
     */
    @Override
    public void putUserId(String userId, NetSocket socket) {
        userHandlerMap.put(userId, socket.writeHandlerID());
        userSocketMap.put(userId, socket);
    }

    /**
     * 根据handlerId获取userId
     *
     * @param handlerId
     * @return
     */
    @Override
    public String getUserIdByHandlerId(String handlerId) {
        String userId = "";
        Iterator<Map.Entry<String, String>> iterator = userHandlerMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (handlerId.equals(entry.getValue())) {
                userId = entry.getKey();
                break;
            }
        }
        return userId;
    }

    /**
     * 通过userId获取对应的socket
     *
     * @param userId
     * @return
     */
    @Override
    public NetSocket getSocketByUserId(String userId) {
        return userSocketMap.get(userId);
    }
}
