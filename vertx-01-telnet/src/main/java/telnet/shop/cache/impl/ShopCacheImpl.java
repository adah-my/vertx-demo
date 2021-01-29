package telnet.shop.cache.impl;

import telnet.shop.cache.ShopCache;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author muyi
 * @description:
 * @date 2020-11-05 19:44:11
 */
public class ShopCacheImpl implements ShopCache {

    ConcurrentHashMap<String, Integer> shopItems;

    private volatile static ShopCache instance;

    private ShopCacheImpl() {
        shopItems = new ConcurrentHashMap<String, Integer>();
    }

    /**
     * @return ShopCache
     */
    public static ShopCache getInstance() {
        if (instance == null) {
            synchronized (ShopCacheImpl.class) {
                if (instance == null) {
                    instance = new ShopCacheImpl();
                }
            }
        }
        return instance;
    }

    /**
     * 更新用户商店道具内存
     *
     * @param itemName
     * @param itemCount
     */
    @Override
    public void saveUserShopItems(String itemName, int itemCount) {
        shopItems.put(itemName, itemCount);
    }

    /**
     * 根据道具名获取道具数量
     *
     * @param itemName
     * @return
     */
    @Override
    public int getItemCountByName(String itemName) {
        return shopItems.get(itemName);
    }

    /**
     * 初始化所有用户的商店道具
     */
    @Override
    public void initAllUserItems() {
        Iterator<Map.Entry<String, Integer>> iterator = shopItems.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            if (entry.getValue() != 10) {
                entry.setValue(10);
            }
        }
    }
}
