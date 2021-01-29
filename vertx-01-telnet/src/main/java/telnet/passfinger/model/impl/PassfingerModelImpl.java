package telnet.passfinger.model.impl;

import telnet.passfinger.cache.PassfingerCache;
import telnet.passfinger.cache.bean.ItemCacheBean;
import telnet.passfinger.dao.PassfingerDao;
import telnet.passfinger.dao.bean.RecordDaoBean;
import telnet.passfinger.model.PassfingerModel;
import telnet.util.RedisUtil;

/**
 * @author muyi
 * @description:
 * @date 2020-11-09 10:16:48
 */
public class PassfingerModelImpl implements PassfingerModel {

    private volatile static PassfingerModel instance;
    PassfingerCache passfingerCache;
    RedisUtil redis;
    PassfingerDao passfingerDao;

    private PassfingerModelImpl() {
        passfingerCache = PassfingerCache.getInstance();
        redis = RedisUtil.getInstance();
        passfingerDao = PassfingerDao.getInstance();
    }

    /**
     * @return PassfingerModel
     */
    public static PassfingerModel getInstance() {
        if (instance == null) {
            synchronized (PassfingerModelImpl.class) {
                if (instance == null) {
                    instance = new PassfingerModelImpl();
                }
            }
        }
        return instance;
    }

    /**
     * 返回道具数量
     *
     * @param userId
     * @return
     */
    @Override
    public int getItemsCount(String userId) {
        Integer itemCount = passfingerCache.getItemCountByName(userId + "-pass");
        if (itemCount == null) {
            String countStr = redis.get(userId + "-pass");
            passfingerCache.addUserItems(userId + "-pass", 0);
            if (countStr == null) {
                RecordDaoBean userRecord = passfingerDao.getUserRecord(userId);
                if (userRecord == null) {
                    itemCount = 0;
                } else {
                    itemCount = userRecord.getItems().get(0).getItemCount();
                }
            } else {
                itemCount = Integer.parseInt(countStr);
            }
        }
        return itemCount;
    }

    /**
     * 添加游戏道具数量
     *
     * @param userId
     * @param buyCount
     */
    @Override
    public void addUserItems(String userId, int buyCount) {
        ItemCacheBean userItem = passfingerDao.getUserItem(userId);
        int newItemsCount = userItem.getItemCount() + buyCount;
        // 内存
        passfingerCache.addUserItems(userId + "-pass", newItemsCount);
        // redis
        redis.set(userId + "-pass", newItemsCount + "");
        // 数据库
        passfingerDao.saveUserItems(userId, newItemsCount);
    }

}
