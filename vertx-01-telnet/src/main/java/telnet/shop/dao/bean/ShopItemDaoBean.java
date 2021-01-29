package telnet.shop.dao.bean;

/**
 * @author muyi
 * @description:
 * @date 2020-11-05 18:55:03
 */
public class ShopItemDaoBean {

    /**
     * 道具名字
     */
    private String itemName;
    /**
     * 道具数量
     */
    private int itemCount;

    public ShopItemDaoBean(String itemName, int itemCount) {
        this.itemName = itemName;
        this.itemCount = itemCount;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    @Override
    public String toString() {
        return "ShopItemDaoBean{" +
                "itemName='" + itemName + '\'' +
                ", itemCount=" + itemCount +
                '}';
    }
}
