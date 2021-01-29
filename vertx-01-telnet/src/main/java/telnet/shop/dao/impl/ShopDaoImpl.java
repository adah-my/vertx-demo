package telnet.shop.dao.impl;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import telnet.shop.dao.ShopDao;
import telnet.shop.dao.bean.ShopItemDaoBean;

/**
 * @author muyi
 * @description:
 * @date 2020-11-05 18:49:25
 */
public class ShopDaoImpl implements ShopDao {

    private volatile static ShopDao instance;

    private ShopDaoImpl() {

    }

    /**
     * @return PassFingerDao
     */
    public static ShopDao getInstance() {
        if (instance == null) {
            synchronized (ShopDaoImpl.class) {
                if (instance == null) {
                    instance = new ShopDaoImpl();
                }
            }
        }
        return instance;
    }

    /**
     * 获取商店道具
     *
     * @param userId
     */
    @Override
    public ShopItemDaoBean findShopItemsByUserId(String userId) {
        Record fingerRecord = Db.findById("user_shop", userId);

        ShopItemDaoBean shopItems;
        if (fingerRecord == null) {
            shopItems = null;
        } else {
            shopItems = new ShopItemDaoBean(fingerRecord.getStr("item_name"), fingerRecord.getInt("item_count"));
        }
        return shopItems;
    }

    /**
     * 保存更新后的道具数
     *
     * @param userId
     * @param shopItemDaoBean
     */
    @Override
    public void saveItemCount(String userId, ShopItemDaoBean shopItemDaoBean) {
        Record shopItem = new Record().set("id", userId).set("item_name", shopItemDaoBean.getItemName()).set("item_count", shopItemDaoBean.getItemCount());
        saveOrUpdate("user_shop", userId, shopItem);
    }

    /**
     * 清除表中所有的数据
     */
    @Override
    public void delAllShopItems() {
        Db.delete("DELETE FROM user_shop");
    }

    /**
     * 存在则更新不存在则保存
     *
     * @param tableName
     * @param tableId
     * @param record
     */
    private void saveOrUpdate(String tableName, String tableId, Record record) {
        Record existRecord = Db.findById(tableName, tableId);
        if (existRecord == null) {
            Db.save(tableName, record);
        } else {
            Db.update(tableName, record);
        }
    }

}
