package telnet.strength.model.impl;


import telnet.strength.cache.StrengthCache;
import telnet.strength.cache.bean.StrengthCacheBean;
import telnet.strength.dao.StrengthDao;
import telnet.strength.model.StrengthModel;
import telnet.util.RedisUtil;

/**
 * @author muyi
 * @description:
 * @date 2020-11-06 18:22:47
 */
public class StrengthModelImpl implements StrengthModel {

    StrengthCache strengthCache;
    StrengthDao strengthDao;
    RedisUtil redis;

    private volatile static StrengthModel instance;

    private StrengthModelImpl() {
        strengthCache = StrengthCache.getInstance();
        strengthDao = StrengthDao.getInstance();
        redis = RedisUtil.getInstance();
    }

    /**
     * @return StrengthModel
     */
    public static StrengthModel getInstance() {
        if (instance == null) {
            synchronized (StrengthModelImpl.class) {
                if (instance == null) {
                    instance = new StrengthModelImpl();
                }
            }
        }
        return instance;
    }

    /**
     * 体力减一
     * @param userId
     */
    @Override
    public void minusOneStrength(String userId) {
        String strength = redis.hget("userStrength", userId);
        String strengthPurchase = redis.hget("userStrengthPurchase", userId);
        StrengthCacheBean strengthCacheBean;
        if (strength == null){
            strengthCacheBean = strengthDao.findUserStrengthById(userId);
            strengthCacheBean.setStrengthCount(strengthCacheBean.getStrengthCount()-1);
        } else {
            strengthCacheBean = new StrengthCacheBean(userId, Integer.parseInt(strength)-1, Integer.parseInt(strengthPurchase));

        }
        strengthCache.updateUserStrength(userId,strengthCacheBean);
        redis.hset("userStrength", userId,strengthCacheBean.getStrengthCount()+"");
        redis.hset("userStrengthPurchase", userId,strengthCacheBean.getStrengthPurchase()+"");
        strengthDao.saveUserStrength(strengthCacheBean);
    }

    /**
     * 获取用户体力
     * @param userId
     * @return
     */
    @Override
    public int getUserStrength(String userId) {
        return strengthCache.getUserStrengthById(userId).getStrengthCount();
    }
}
