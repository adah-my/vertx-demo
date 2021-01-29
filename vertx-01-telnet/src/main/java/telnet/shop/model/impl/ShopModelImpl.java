package telnet.shop.model.impl;

import telnet.shop.cache.ShopCache;
import telnet.shop.dao.bean.ShopItemDaoBean;
import telnet.shop.model.ShopModel;


/**
 * @author muyi
 * @description:
 * @date 2020-11-05 20:37:20
 */
public class ShopModelImpl implements ShopModel {


    private ShopCache shopCache;
    private volatile static ShopModel instance;

    private ShopModelImpl() {
        shopCache = ShopCache.getInstance();
    }

    /**
     * @return ShopCache
     */
    public static ShopModel getInstance() {
        if (instance == null) {
            synchronized (ShopModelImpl.class) {
                if (instance == null) {
                    instance = new ShopModelImpl();
                }
            }
        }
        return instance;
    }

    /**
     * 获取商店道具数
     * @param userId
     * @return
     */
    @Override
    public ShopItemDaoBean getShopItemByUserId(String userId) {
        int itemCount = shopCache.getItemCountByName(userId + "-shop-pass");
        return new ShopItemDaoBean(userId + "-shop-pass",itemCount);
    }
}
