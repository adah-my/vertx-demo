package telnet.strength.cache.impl;

import telnet.shop.cache.impl.ShopCacheImpl;
import telnet.strength.cache.StrengthCache;
import telnet.strength.cache.bean.StrengthCacheBean;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author muyi
 * @description:
 * @date 2020-11-06 12:00:27
 */
public class StrengthCacheImpl implements StrengthCache {

    private ConcurrentHashMap<String, StrengthCacheBean> userStrength;

    private volatile static StrengthCache instance;

    private StrengthCacheImpl() {
        userStrength = new ConcurrentHashMap<String, StrengthCacheBean>();
    }

    /**
     * @return ShopCache
     */
    public static StrengthCache getInstance() {
        if (instance == null) {
            synchronized (ShopCacheImpl.class) {
                if (instance == null) {
                    instance = new StrengthCacheImpl();
                }
            }
        }
        return instance;
    }

    /**
     * 更新用户体力
     *
     * @param userId
     * @param userStrengthBean
     */
    @Override
    public void updateUserStrength(String userId, StrengthCacheBean userStrengthBean) {
        userStrength.put(userId, userStrengthBean);
    }

    /**
     * 获取体力bean
     *
     * @param userId
     * @return
     */
    @Override
    public StrengthCacheBean getUserStrengthById(String userId) {
        return userStrength.get(userId);
    }

    /**
     * 所有不满体力的用户体力+1
     */
    @Override
    public void replayUserStrength() {
        Iterator<Map.Entry<String, StrengthCacheBean>> iterator = userStrength.entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<String, StrengthCacheBean> entry = iterator.next();
            int strengthCount = entry.getValue().getStrengthCount();
            if (strengthCount < 100) {
                entry.getValue().setStrengthCount(strengthCount + 1);
            }
        }
    }

    /**
     * 刷新体力购买次数
     */
    @Override
    public void initStrengthParchase() {
        Iterator<Map.Entry<String, StrengthCacheBean>> iterator = userStrength.entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<String, StrengthCacheBean> entry = iterator.next();
            int strengthPurchase = entry.getValue().getStrengthPurchase();
            if (strengthPurchase != 100) {
                entry.getValue().setStrengthCount(0);
            }
        }
    }

}
