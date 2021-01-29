package telnet.strength.dao.impl;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import telnet.strength.cache.bean.StrengthCacheBean;
import telnet.strength.dao.StrengthDao;

import java.util.ArrayList;
import java.util.List;

/**
 * @author muyi
 * @description:
 * @date 2020-11-06 12:21:13
 */
public class StrengthDaoImpl implements StrengthDao {

    private volatile static StrengthDao instance;

    private StrengthDaoImpl() {
    }

    /**
     * @return StrengthDao
     */
    public static StrengthDao getInstance() {
        if (instance == null) {
            synchronized (StrengthDaoImpl.class) {
                if (instance == null) {
                    instance = new StrengthDaoImpl();
                }
            }
        }
        return instance;
    }

    /**
     * 查询体力
     * @param userId
     * @return
     */
    @Override
    public StrengthCacheBean findUserStrengthById(String userId) {

        Record userStrength = Db.findById("user_strength", userId);
        StrengthCacheBean strengthCacheBean;
        if (userStrength == null){
            strengthCacheBean = new StrengthCacheBean(userId,100,0);
        }else {
            strengthCacheBean = new StrengthCacheBean(userId , userStrength.getInt("strength_count") , userStrength.getInt("purchase_count"));
        }
        return strengthCacheBean;
    }

    @Override
    public void saveUserStrength(StrengthCacheBean userStrength) {
        Record strengthRecord = new Record().set("id", userStrength.getUserId()).set("strength_count", userStrength.getStrengthCount()).set("purchase_count",userStrength.getStrengthPurchase());
        saveOrUpdate("user_strength", userStrength.getUserId(), strengthRecord);
    }

    @Override
    public List<StrengthCacheBean> findAllUserStrength() {
        List<Record> userStrength = Db.findAll("user_strength");
        List<StrengthCacheBean> existBeans = new ArrayList<StrengthCacheBean>();
        for (Record record: userStrength){
            existBeans.add(new StrengthCacheBean(record.getStr("id"),record.getInt("strength_count"),record.getInt("purchase_count")));
        }
        return existBeans;

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

//    public static void main(String[] args) {
//        JfinalUtil.getInstance().loadPlugin();
//        StrengthDao strengthDao = StrengthDao.getInstance();
//        System.out.println(strengthDao.findUserStrengthById("bbb"));
//    }
}
