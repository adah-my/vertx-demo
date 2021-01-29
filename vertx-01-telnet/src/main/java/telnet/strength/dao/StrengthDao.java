package telnet.strength.dao;

import telnet.strength.cache.bean.StrengthCacheBean;
import telnet.strength.dao.impl.StrengthDaoImpl;

import java.util.List;

/**
 * @author muyi
 * @description:
 * @date 2020-11-06 12:20:56
 */
public interface StrengthDao {

    static StrengthDao getInstance() {
        return StrengthDaoImpl.getInstance();
    }

    StrengthCacheBean findUserStrengthById(String userId);

    void saveUserStrength(StrengthCacheBean userStrength);

    List<StrengthCacheBean> findAllUserStrength();

}
