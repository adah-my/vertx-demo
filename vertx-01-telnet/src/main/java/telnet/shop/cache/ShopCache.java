package telnet.shop.cache;

import telnet.shop.cache.impl.ShopCacheImpl;

/**
 * @author muyi
 * @description:
 * @date 2020-11-05 19:43:53
 */
public interface ShopCache {

    static ShopCache getInstance() {
        return ShopCacheImpl.getInstance();
    }

    /**
     * 更新用户商店道具内存
     *
     * @param itemName
     * @param itemCount
     */
    void saveUserShopItems(String itemName, int itemCount);

    /**
     * 初始化所有用户的商店道具
     */
    void initAllUserItems();

    /**
     * 根据道具名获取道具数量
     *
     * @param itemName
     * @return
     */
    int getItemCountByName(String itemName);
}
