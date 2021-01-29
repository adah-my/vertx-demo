package telnet.core.config;

import telnet.passfinger.config.PassfingerConfig;
import telnet.shop.util.InitShopItemsUtil;
import telnet.strength.util.InitStrengthParchaseUtil;
import telnet.strength.util.ReplyStrengthUtil;
import telnet.util.JfinalUtil;

/**
 * @author muyi
 * @description: 加载所有配置文件
 * @date 2020-11-05 11:04:03
 */
public class Config {

    private volatile static Config instance;
    private PassfingerConfig passFingerConfig;
    private JfinalUtil jfinalUtil;

    private Config(){
        jfinalUtil = JfinalUtil.getInstance();
        passFingerConfig = PassfingerConfig.getInstance();
    }
    /**
     * @return Config
     */
    public static Config getInstance() {
        if (instance == null) {
            synchronized (Config.class) {
                if (instance == null) {
                    instance = new Config();
                }
            }
        }
        return instance;
    }

    public void loadConfig(){
        jfinalUtil.loadPlugin();
        passFingerConfig.getLevelsFromFile();
    }

    public void startTimerTack(){
        new InitShopItemsUtil().executeAtMidnightPerDay();
        new ReplyStrengthUtil().executeFiveMinutes();
        new InitStrengthParchaseUtil().executeAtMidnightPerDay();
    }

}
