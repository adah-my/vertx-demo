package telnet.passfinger.cache.impl;

import telnet.passfinger.cache.PassfingerCache;
import telnet.passfinger.config.PassfingerConfig;
import telnet.passfinger.config.bean.PassfingerConfigBean;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author muyi
 * @description:
 * @date 2020-11-03 15:30:48
 */
public class PassfingerCacheImpl implements PassfingerCache {

    /**
     * 用户游戏进度记录
     */
    private ConcurrentHashMap<String, String> userRecords;
    /**
     * 用户游戏道具记录
     */
    private ConcurrentHashMap<String, Integer> userItems;
    /**
     * 关卡列表
     */
    private ConcurrentHashMap<String, PassfingerConfigBean> levels;
    /**
     * 用户游戏游戏保底
     */
    private ConcurrentHashMap<String, Integer> userGuaranteed;
    private volatile static PassfingerCache instance;

    private PassfingerCacheImpl() {
        userRecords = new ConcurrentHashMap<String, String>();
        levels = PassfingerConfig.getInstance().getLevelsFromFile();
        userItems = new ConcurrentHashMap<String, Integer>();
        userGuaranteed =  new ConcurrentHashMap<String, Integer>();
    }

    /**
     * @return PassFingerCache
     */
    public static PassfingerCache getInstance() {
        if (instance == null) {
            synchronized (PassfingerCacheImpl.class) {
                if (instance == null) {
                    instance = new PassfingerCacheImpl();
                }
            }
        }
        return instance;
    }

    /**
     * 添加用户游戏记录
     * @param userId
     * @param levelNum
     */
    @Override
    public void addUserRecord(String userId, String levelNum) {
        userRecords.put(userId, levelNum);
    }

    /**
     * 通过关卡id得到当前关卡
     * @param levelNum
     * @return
     */
    @Override
    public PassfingerConfigBean getLevelByNum(String levelNum) {
        return levels.get(levelNum);
    }

    /**
     * 通过用户id得到关卡
     * @param userId
     * @return
     */
    @Override
    public PassfingerConfigBean getLevelByUserId(String userId) {
        String levelNum = userRecords.get(userId);
        return levels.get(levelNum);
    }

    /**
     * 更新用户游戏进度
     * @param userId
     * @param levelNum
     */
    @Override
    public void setUserRecord(String userId, String levelNum) {
        userRecords.put(userId, levelNum);
    }



    /**
     * 添加道具
     * @param itemName
     * @param Num
     */
    @Override
    public void addUserItems(String itemName, int Num) {
        userItems.put(itemName, Num);
    }

    /**
     * 查询道具数量
     * @param itemName
     * @return
     */
    @Override
    public Integer getItemCountByName(String itemName) {
        return userItems.get(itemName);
    }

    @Override
    public void addUserGuaranteed(String userId) {
        Integer userGuarante = userGuaranteed.get(userId + "-guaranteed");
        if (userGuarante == null){
            userGuaranteed.put(userId+"-guaranteed",1);
        }else {
            userGuaranteed.put(userId+"-guaranteed",userGuarante+1);
        }
    }

    /**
     * 获取用户失败保底数次
     * @param userId
     * @return
     */
    @Override
    public int getUserGuaranteed(String userId) {
        Integer userGuarante = userGuaranteed.get(userId + "-guaranteed");
        if (userGuarante == null || userGuarante == 0){
            userGuarante = 0;
        }
        return userGuarante;
    }

    /**
     * 删除已触发保底的记录
     * @param userId
     */
    @Override
    public void delUserGuaranete(String userId) {
        userGuaranteed.remove(userId+"-guaranteed");
    }

}
