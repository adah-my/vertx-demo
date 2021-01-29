package telnet.strength.cache.bean;

/**
 * @author muyi
 * @description:
 * @date 2020-11-06 14:58:02
 */
public class StrengthCacheBean {

    private String userId;
    private int strengthCount;
    private int StrengthPurchase;

    public StrengthCacheBean(String userId, int strengthCount, int strengthPurchase) {
        this.userId = userId;
        this.strengthCount = strengthCount;
        StrengthPurchase = strengthPurchase;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getStrengthCount() {
        return strengthCount;
    }

    public void setStrengthCount(int strengthCount) {
        this.strengthCount = strengthCount;
    }

    public int getStrengthPurchase() {
        return StrengthPurchase;
    }

    public void setStrengthPurchase(int strengthPurchase) {
        StrengthPurchase = strengthPurchase;
    }

    @Override
    public String toString() {
        return "StrengthCacheBean{" +
                "userId='" + userId + '\'' +
                ", strengthCount=" + strengthCount +
                ", StrengthPurchase=" + StrengthPurchase +
                '}';
    }
}
