package telnet.strength.cache;

import telnet.strength.cache.bean.StrengthCacheBean;
import telnet.strength.cache.impl.StrengthCacheImpl;

/**
 * @author muyi
 * @description:
 * @date 2020-11-06 11:59:26
 */
public interface StrengthCache {

    static StrengthCache getInstance() {
        return StrengthCacheImpl.getInstance();
    }


    /**
     * 更新用户体力
     *
     * @param userId
     * @param userStrength
     */
    void updateUserStrength(String userId, StrengthCacheBean userStrength);


    /**
     * 获取体力bean
     *
     * @param userId
     * @return
     */
    StrengthCacheBean getUserStrengthById(String userId);

    /**
     * 所有不满体力的用户体力+1
     */
    void replayUserStrength();

    /**
     * 刷新体力购买次数
     */
    void initStrengthParchase();
}
