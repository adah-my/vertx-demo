package telnet.passfinger.config;

import telnet.passfinger.config.bean.PassfingerConfigBean;
import telnet.passfinger.config.impl.PassfingerConfigImpl;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author muyi
 * @description:
 * @date 2020-11-05 10:45:49
 */
public interface PassfingerConfig {

    static PassfingerConfig getInstance(){
        return PassfingerConfigImpl.getInstance();
    }

    /**
     * 读取配置文件中的关卡
     * @return
     */
    ConcurrentHashMap<String, PassfingerConfigBean> getLevelsFromFile();
    
}
