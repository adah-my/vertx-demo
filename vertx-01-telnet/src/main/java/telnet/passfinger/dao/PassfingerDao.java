package telnet.passfinger.dao;

import telnet.passfinger.cache.bean.ItemCacheBean;
import telnet.passfinger.dao.bean.RecordDaoBean;
import telnet.passfinger.dao.impl.PassfingerDaoImpl;

/**
 * @author muyi
 * @description:
 * @date 2020-11-09 16:36:54
 */
public interface PassfingerDao {

    static PassfingerDao getInstance() {
        return PassfingerDaoImpl.getInstance();
    }

    /**
     * 将用户存档存入数据库
     *
     * @param userId
     * @param levelNum
     */
    void saveUserRecord(String userId, String levelNum);

    /**
     * 读取用户存档
     *
     * @param userId
     * @return
     */
    RecordDaoBean getUserRecord(String userId);

    /**
     * 判断是否存在用户存档
     *
     * @param userId
     * @return
     */
    boolean existUserRecord(String userId);

    /**
     * 保存更新用户道具
     *
     * @param userId
     * @param itemCount
     */
    void saveUserItems(String userId, int itemCount);

    /**
     * 获取用户道具数量
     *
     * @param userId
     */
    ItemCacheBean getUserItem(String userId);
}
