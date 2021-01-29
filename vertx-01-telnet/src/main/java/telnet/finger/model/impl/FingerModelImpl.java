package telnet.finger.model.impl;

import telnet.finger.cache.FingerCache;
import telnet.finger.model.FingerModel;

/**
 * @author muyi
 * @description:
 * @date 2020-11-02 16:10:53
 */
public class FingerModelImpl implements telnet.finger.model.FingerModel {

    FingerCache fingerCache;

    private volatile static FingerModel instance;

    private FingerModelImpl(){
        fingerCache = FingerCache.getInstance();
    }

    /**
     * @return FingerCache
     */
    public static FingerModel getInstance() {
        if (instance == null) {
            synchronized (FingerModelImpl.class) {
                if (instance == null) {
                    instance = new FingerModelImpl();
                }
            }
        }
        return instance;
    }

    /**
     * 模糊删除
     *
     * @param chatroomName
     */
    @Override
    public void delFingerGuessByChatroomName(String chatroomName) {
        fingerCache.delFingerGuessGroupByChatroomName(chatroomName);
    }
}
