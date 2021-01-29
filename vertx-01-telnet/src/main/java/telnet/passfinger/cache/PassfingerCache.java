package telnet.passfinger.cache;

import telnet.passfinger.cache.impl.PassfingerCacheImpl;
import telnet.passfinger.config.bean.PassfingerConfigBean;

/**
 * @author muyi
 * @description:
 * @date 2020-11-09 16:34:37
 */
public interface PassfingerCache {
    static PassfingerCache getInstance() {
        return PassfingerCacheImpl.getInstance();
    }
    /**
     * 添加用户游戏记录
     * @param userId
     * @param levelNum
     */
    void addUserRecord(String userId, String levelNum);

    /**
     * 通过关卡id得到当前关卡
     * @param levelNum
     * @return
     */
    PassfingerConfigBean getLevelByNum(String levelNum);

    /**
     * 通过用户id得到关卡
     * @param userId
     * @return
     */
    PassfingerConfigBean getLevelByUserId(String userId);

    /**
     * 更新用户游戏进度
     * @param userId
     * @param levelNum
     */
    void setUserRecord(String userId, String levelNum);

    /**
     * 添加道具
     * @param itemName
     * @param Num
     */
    void addUserItems(String itemName, int Num);

    /**
     * 查询道具数量
     * @param itemName
     * @return
     */
    Integer getItemCountByName(String itemName);

    void addUserGuaranteed(String userId);

    /**
     * 获取用户失败保底数次
     * @param userId
     * @return
     */
    int getUserGuaranteed(String userId);

    /**
     * 删除已触发保底的记录
     * @param userId
     */
    void delUserGuaranete(String userId);
}
