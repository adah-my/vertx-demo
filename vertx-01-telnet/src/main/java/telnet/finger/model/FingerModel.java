package telnet.finger.model;

import telnet.finger.model.impl.FingerModelImpl;

/**
 * @author muyi
 * @description:
 * @date 2020-11-03 12:11:57
 */
public interface FingerModel {
    /**
     * @return FingerModel
     */
    static FingerModel getInstance() {
        return FingerModelImpl.getInstance();
    }

    /**
     * 模糊删除
     *
     * @param chatroomName
     */
    void delFingerGuessByChatroomName(String chatroomName);
}
