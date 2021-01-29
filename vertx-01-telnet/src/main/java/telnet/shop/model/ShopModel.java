package telnet.shop.model;

import telnet.shop.dao.bean.ShopItemDaoBean;
import telnet.shop.model.impl.ShopModelImpl;

/**
 * @author muyi
 * @description:
 * @date 2020-11-05 20:37:01
 */
public interface ShopModel {

    static ShopModel getInstance() {
        return ShopModelImpl.getInstance();
    }

    /**
     * 获取商店道具数
     *
     * @param userId
     * @return
     */
    ShopItemDaoBean getShopItemByUserId(String userId);
}
