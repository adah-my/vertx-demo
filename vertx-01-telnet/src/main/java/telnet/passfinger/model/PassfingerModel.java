package telnet.passfinger.model;

import telnet.passfinger.model.impl.PassfingerModelImpl;

/**
 * @author muyi
 * @description:
 * @date 2020-11-09 10:16:28
 */
public interface PassfingerModel {

    /**
     * 返回接口实例
     *
     * @return
     */
    static PassfingerModel getInstance() {
        return PassfingerModelImpl.getInstance();
    }

    /**
     * 返回道具数量
     *
     * @param userId
     * @return
     */
    int getItemsCount(String userId);

    /**
     * 添加游戏道具数量
     *
     * @param userId
     * @param buyCount
     */
    void addUserItems(String userId, int buyCount);
}
