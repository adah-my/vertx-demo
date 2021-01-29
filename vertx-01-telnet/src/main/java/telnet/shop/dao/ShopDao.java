package telnet.shop.dao;

import telnet.shop.dao.bean.ShopItemDaoBean;
import telnet.shop.dao.impl.ShopDaoImpl;

/**
 * @author muyi
 * @description:
 * @date 2020-11-05 18:48:03
 */
public interface ShopDao {

    static ShopDao getInstance() {
        return ShopDaoImpl.getInstance();
    }


    /**
     * 获取商店道具
     *
     * @param userId
     */
    ShopItemDaoBean findShopItemsByUserId(String userId);

    /**
     * 保存更新后的道具数
     *
     * @param userId
     * @param shopItemDaoBean
     */
    void saveItemCount(String userId, ShopItemDaoBean shopItemDaoBean);

    /**
     * 清除表中所有的数据
     */
    void delAllShopItems();
}
