package telnet.passfinger.dao.impl;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import telnet.passfinger.cache.bean.ItemCacheBean;
import telnet.passfinger.dao.PassfingerDao;
import telnet.passfinger.dao.bean.RecordDaoBean;

/**
 * @author muyi
 * @description:
 * @date 2020-11-05 10:50:45
 */
public class PassfingerDaoImpl implements PassfingerDao {

    private volatile static PassfingerDao instance;

    private PassfingerDaoImpl() {

    }

    /**
     * @return PassFingerDao
     */
    public static PassfingerDao getInstance() {
        if (instance == null) {
            synchronized (PassfingerDaoImpl.class) {
                if (instance == null) {
                    instance = new PassfingerDaoImpl();
                }
            }
        }
        return instance;
    }

    /**
     * 将用户存档存入数据库
     *
     * @param userId
     * @param levelNum
     */
    @Override
    public void saveUserRecord(String userId, String levelNum) {
        RecordDaoBean recordDaoBean = new RecordDaoBean(userId, userId + "-passfinger", levelNum);
        // 存关卡
        Record fingerRecord = new Record().set("id", recordDaoBean.getUserId()).set("record_name", recordDaoBean.getRecordName()).set("level_id", recordDaoBean.getLevelId());
        saveOrUpdate("finger_record", recordDaoBean.getUserId(), fingerRecord);

    }

    /**
     * 读取用户存档
     *
     * @param userId
     * @return
     */
    @Override
    public RecordDaoBean getUserRecord(String userId) {
        Record fingerRecord = Db.findById("finger_record", userId);

        RecordDaoBean recordDaoBean;
        if (fingerRecord == null) {
            return null;
        } else {
            recordDaoBean = new RecordDaoBean(userId, fingerRecord.getStr("record_name"), fingerRecord.getStr("level_id"));
        }

        return recordDaoBean;
    }

    /**
     * 判断是否存在用户存档
     *
     * @param userId
     * @return
     */
    @Override
    public boolean existUserRecord(String userId) {
        Record fingerRecord = Db.findById("finger_record", userId);
        return fingerRecord != null;
    }

    /**
     * 保存更新用户道具
     *
     * @param userId
     * @param itemCount
     */
    @Override
    public void saveUserItems(String userId, int itemCount) {
        Record userItem = new Record().set("id", userId).set("item_name", userId + "-pass").set("item_count", itemCount);
        saveOrUpdate("user_items", userId, userItem);
    }

    /**
     * 获取用户道具数量
     *
     * @param userId
     */
    @Override
    public ItemCacheBean getUserItem(String userId) {
        Record userItem = Db.findById("user_items", userId);
        ItemCacheBean item;
        if (userItem == null) {
            item = new ItemCacheBean(userId + "-pass", 0);
        } else {
            item = new ItemCacheBean(userItem.getStr("item_name"), userItem.getInt("item_count"));
        }
        return item;
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
