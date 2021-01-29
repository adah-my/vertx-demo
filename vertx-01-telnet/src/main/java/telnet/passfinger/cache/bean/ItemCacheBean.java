package telnet.passfinger.cache.bean;

/**
 * @author muyi
 * @description:
 * @date 2020-11-05 11:49:09
 */
public class ItemCacheBean {

    /**
     * 道具名
     */
    private String itemName;
    /**
     * 道具数量
     */
    private int itemCount;

    public ItemCacheBean(String itemName, int itemCount) {
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
        return "ItemCacheBean{" +
                "itemName='" + itemName + '\'' +
                ", itemCount=" + itemCount +
                '}';
    }
}
