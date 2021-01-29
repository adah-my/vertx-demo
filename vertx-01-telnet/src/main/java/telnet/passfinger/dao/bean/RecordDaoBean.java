package telnet.passfinger.dao.bean;

import telnet.passfinger.cache.bean.ItemCacheBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author muyi
 * @description:
 * @date 2020-11-05 10:50:24
 */
public class RecordDaoBean {

    private String userId;
    private String recordName;
    private String levelId;
    private List<ItemCacheBean> items;

    public RecordDaoBean(String userId, String recordName, String levelId) {
        this.userId = userId;
        this.recordName = recordName;
        this.levelId = levelId;
        items = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public List<ItemCacheBean> getItems() {
        return items;
    }

    public void setItems(List<ItemCacheBean> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "RecordDaoBean{" +
                "userId='" + userId + '\'' +
                ", recordName='" + recordName + '\'' +
                ", levelId=" + levelId +
                ", items=" + items +
                '}';
    }
}
