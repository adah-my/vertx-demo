package telnet.core.model;

import io.vertx.core.net.NetSocket;
import telnet.core.model.impl.UserModelImpl;

/**
 * @author muyi
 * @description:
 * @date 2020-11-02 16:55:02
 */
public interface UserModel {

    static UserModel getInstance() {
        return UserModelImpl.getInstance();
    }
    /**
     * 测试
     *
     * @return
     */
    @Override
    String toString();

    /**
     * 删除userId
     *
     * @param userId
     */
    void removeUserId(String userId);

    /**
     * 添加UserId
     *
     * @param userId
     * @param socket
     */
    void putUserId(String userId, NetSocket socket);

    /**
     * 根据handlerId获取userId
     *
     * @param handlerId
     * @return
     */
    String getUserIdByHandlerId(String handlerId);

    /**
     * 通过userId获取对应的socket
     *
     * @param userId
     * @return
     */
    NetSocket getSocketByUserId(String userId);
}
