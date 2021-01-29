package telnet.passfinger.cmds;

import telnet.core.common.CmdsResponseBody;
import telnet.login.model.LoginModel;
import telnet.passfinger.cache.PassfingerCache;
import telnet.passfinger.cache.bean.PassfingerCacheBean;
import telnet.passfinger.cache.bean.PassfingerGroupCacheBean;
import telnet.passfinger.config.bean.PassfingerConfigBean;
import telnet.passfinger.dao.PassfingerDao;
import telnet.passfinger.dao.bean.RecordDaoBean;
import telnet.strength.model.StrengthModel;
import telnet.util.GuideUtil;
import telnet.util.RedisUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author muyi
 * @description:
 * @date 2020-11-03 14:25:00
 */
public class PassfingerCmds {

    LoginModel loginModel;
    PassfingerCache passFingerCache;
    PassfingerDao passFingerDao;
    StrengthModel strengthModel;
    RedisUtil redis;
    /**
     * 日志
     */
    public static final Logger log = Logger.getLogger(PassfingerCmds.class.getName());

    public PassfingerCmds() {
        loginModel = LoginModel.getInstance();
        passFingerCache = PassfingerCache.getInstance();
        passFingerDao = PassfingerDao.getInstance();
        strengthModel = StrengthModel.getInstance();
        redis = RedisUtil.getInstance();
    }

    /**
     * 进入猜拳打关游戏
     *
     * @param commands
     * @return
     */
    public List<CmdsResponseBody> passfinger(String userId, String[] commands) {
        ArrayList<CmdsResponseBody> bodys = new ArrayList<>();
        CmdsResponseBody body = new CmdsResponseBody();

        // 1.校验参数
        if ("".equals(userId) || !loginModel.isUserOnline(userId)) {
            body.getMessages().add("您还没有登陆，请先登录！");
        } else if (GuideUtil.userGuide.get(userId) == 2) {
            body.getMessages().add("您正在聊天房间中，如需进行游戏，请先退出房间！");
        } else if (GuideUtil.userGuide.get(userId) == 4) {
            body.getMessages().add("您正在商店界面，如需进行游戏，请先退出房间！");
        } else if (GuideUtil.userGuide.get(userId) == 5) {
            body.getMessages().add("您正在体力界面，如需进行游戏，请先退出房间！");
        } else if (commands.length != 1) {
            body.getMessages().add("请输入正确的命令格式！");
        } else {
            // 2.开始游戏，创建新游戏
            PassfingerConfigBean level = passFingerCache.getLevelByNum("1");
            passFingerCache.addUserRecord(userId, "1");
            strengthModel.minusOneStrength(userId);
            int userStrength = strengthModel.getUserStrength(userId);

            GuideUtil.userGuide.put(userId, 3);

            // 3.返回数据
            dividingLine(body.getMessages());
            body.getMessages().add("== 进入游戏，消耗一点体力！剩余体力：" + userStrength);
            gameStartGuide(userId, body, level);
        }
        body.getUserIds().add(userId);
        GuideUtil.setUserGuide(body.getMessages(), userId);
        bodys.add(body);

        // 4.输出日志
        log.info("用户" + userId + "猜拳打关开始日志：" + bodys.toString());

        return bodys;
    }

    /**
     * 发起猜拳
     *
     * @param commands
     * @return
     */
    public List<CmdsResponseBody> atk(String userId, String[] commands) {
        ArrayList<CmdsResponseBody> bodys = new ArrayList<>();
        CmdsResponseBody body = new CmdsResponseBody();

        // 1.校验参数
        if ("".equals(userId) || !loginModel.isUserOnline(userId)) {
            body.getMessages().add("您还没有登陆，请先登录！");
            GuideUtil.setUserGuide(body.getMessages(), userId);
        } else if (GuideUtil.userGuide.get(userId) == 1) {
            body.getMessages().add("您正在主界面中，如需进行游戏，请先进入游戏！");
            GuideUtil.setUserGuide(body.getMessages(), userId);
        } else if (GuideUtil.userGuide.get(userId) == 2) {
            body.getMessages().add("您正在聊天房间中，如需进行游戏，请先退出房间！");
            GuideUtil.setUserGuide(body.getMessages(), userId);
        } else if (GuideUtil.userGuide.get(userId) == 4) {
            body.getMessages().add("您正在商店界面，如需进行游戏，请先退出房间！");
            GuideUtil.setUserGuide(body.getMessages(), userId);
        } else if (GuideUtil.userGuide.get(userId) == 5) {
            body.getMessages().add("您正在体力界面，如需进行游戏，请先退出房间！");
            GuideUtil.setUserGuide(body.getMessages(), userId);
        } else if (commands.length != 2 || PassfingerCacheBean.getFingerGuessByName(commands[1]) == null) {
            body.getMessages().add("请输入正确的命令格式！");
            GuideUtil.setUserGuide(body.getMessages(), userId);
        } else {
            // 2.1 获取用户猜拳值
            PassfingerCacheBean fingerGuess = PassfingerCacheBean.getFingerGuessByName(commands[1]);
            PassfingerConfigBean level = passFingerCache.getLevelByUserId(userId);

            // 2.2 判断关卡的概率，并根据胜率返回怪物的猜拳值
            int userGuaranete = passFingerCache.getUserGuaranteed(userId);
            PassfingerCacheBean monsterFinger_rand;
            if (userGuaranete < 9) {
                monsterFinger_rand = getMonsterFingerByRandom(level.getLevelId(), fingerGuess);
            } else {
                monsterFinger_rand = PassfingerCacheBean.getLoseFinger(fingerGuess);
                body.getMessages().add("累积失败达9次，本轮触发胜利保底！");
                passFingerCache.delUserGuaranete(userId);
            }
            PassfingerGroupCacheBean passFingerGroup = new PassfingerGroupCacheBean(level.getMonsterName(), monsterFinger_rand);

            // 2.3 判断胜利者
            getWinner(userId, fingerGuess, passFingerGroup, body, level.getLevelId());
            // 3.返回数据
        }
        body.getUserIds().add(userId);
        bodys.add(body);

        // 4.输出日志
        log.info("用户" + userId + "猜拳打关发起猜拳日志：" + bodys.toString());

        return bodys;
    }

    /**
     * 根据胜率判断输赢
     *
     * @param levelId
     * @param fingerGuess
     * @return
     */
    private PassfingerCacheBean getMonsterFingerByRandom(String levelId, PassfingerCacheBean fingerGuess) {
        int levelNum = Integer.parseInt(levelId);
        int randomNum = (int) (1 + Math.random() * (10 - 1 + 1));
        PassfingerCacheBean monsterFinger;
        System.out.println(levelNum + " " + randomNum);
        if (levelNum < 9) {
            // 前10关胜利概率分别为90%、80%、70%，一次类推到10%
            if (randomNum > levelNum) {
                monsterFinger = PassfingerCacheBean.getLoseFinger(fingerGuess);
            } else {
                monsterFinger = PassfingerCacheBean.getWinnerFinger(fingerGuess);
            }
        } else {
            // 第9关开始胜率为10%
            if (randomNum == 1) {
                monsterFinger = PassfingerCacheBean.getLoseFinger(fingerGuess);
            } else {
                monsterFinger = PassfingerCacheBean.getWinnerFinger(fingerGuess);
            }
        }
        return monsterFinger;
    }

    /**
     * 记录存档
     *
     * @param commands
     * @return
     */
    public List<CmdsResponseBody> save(String userId, String[] commands) {
        ArrayList<CmdsResponseBody> bodys = new ArrayList<>();
        CmdsResponseBody body = new CmdsResponseBody();

        // 1.校验参数
        if ("".equals(userId) || !loginModel.isUserOnline(userId)) {
            body.getMessages().add("您还没有登陆，请先登录！");
        } else if (GuideUtil.userGuide.get(userId) == 1) {
            body.getMessages().add("您正在主界面中，如需进行游戏，请先进入游戏！");
        } else if (GuideUtil.userGuide.get(userId) == 2) {
            body.getMessages().add("您正在聊天房间中，如需进行游戏，请先退出房间！");
        } else if (GuideUtil.userGuide.get(userId) == 4) {
            body.getMessages().add("您正在商店界面，如需进行游戏，请先退出房间！");
        } else if (GuideUtil.userGuide.get(userId) == 5) {
            body.getMessages().add("您正在体力界面，如需进行游戏，请先退出房间！");
        } else if (commands.length != 1) {
            body.getMessages().add("请输入正确的命令格式！");
        } else if ("1".equals(passFingerCache.getLevelByUserId(userId).getLevelId())) {
            body.getMessages().add("您正处于初始起点，无需存档！！");
        } else {
            // 2.1 用户游戏存档
            PassfingerConfigBean level = passFingerCache.getLevelByUserId(userId);
            String recordName = userId + "-passfinger";
            // 2.2 redis存档
            redis.set(recordName, level.getLevelId());
            // 2.3 更新到mysql中
            passFingerDao.saveUserRecord(userId, level.getLevelId());

            // 3.返回数据
            dividingLine(body.getMessages());
            body.getMessages().add("== 游戏播报：记录存档成功！");
        }
        body.getUserIds().add(userId);
        GuideUtil.setUserGuide(body.getMessages(), userId);
        bodys.add(body);

        // 4.输出日志
        log.info("用户" + userId + "记录存档日志：" + bodys.toString());

        return bodys;
    }

    /**
     * 读取存档
     *
     * @param commands
     * @return
     */
    public List<CmdsResponseBody> load(String userId, String[] commands) {
        ArrayList<CmdsResponseBody> bodys = new ArrayList<>();
        CmdsResponseBody body = new CmdsResponseBody();

        // 1.校验参数
        if ("".equals(userId) || !loginModel.isUserOnline(userId)) {
            body.getMessages().add("您还没有登陆，请先登录！");
        } else if (GuideUtil.userGuide.get(userId) == 1) {
            body.getMessages().add("您正在主界面中，如需进行游戏，请先进入游戏！");
        } else if (GuideUtil.userGuide.get(userId) == 2) {
            body.getMessages().add("您正在聊天房间中，如需进行游戏，请先退出房间！");
        } else if (GuideUtil.userGuide.get(userId) == 4) {
            body.getMessages().add("您正在商店界面，如需进行游戏，请先退出房间！");
        } else if (GuideUtil.userGuide.get(userId) == 5) {
            body.getMessages().add("您正在体力界面，如需进行游戏，请先退出房间！");
        } else if (commands.length != 1) {
            body.getMessages().add("请输入正确的命令格式！");
        } else if (redis.get(userId + "-passfinger") == null && !passFingerDao.existUserRecord(userId)) {
            body.getMessages().add("您没有存档，请先进行游戏存档再执行此命令！！！");
        } else {
            // 2.1 从redis中读取存档
            String levelId = redis.get(userId + "-passfinger");
            if (levelId == null || "".equals(levelId)) {
                // 2.2 从mysql中读取存档
                RecordDaoBean recordDaoBean = passFingerDao.getUserRecord(userId);
                levelId = recordDaoBean.getLevelId();
                // 更新到redis缓存
                redis.set(userId + "-passfinger", levelId);
            }
            // 2.3 更新存档到内存
            passFingerCache.addUserRecord(userId, levelId);

            // 3.返回数据
            PassfingerConfigBean level = passFingerCache.getLevelByNum(levelId);
            dividingLine(body.getMessages());
            body.getMessages().add("== 游戏播报：读取游戏存档成功！！！ ");
            nextLevelGuide(body, level);
        }
        body.getUserIds().add(userId);
        GuideUtil.setUserGuide(body.getMessages(), userId);
        bodys.add(body);

        // 4.输出日志
        log.info("用户" + userId + "读取存档日志：" + bodys.toString());

        return bodys;
    }

    /**
     * 查看背包/使用道具
     *
     * @param commands
     * @return
     */
    public List<CmdsResponseBody> mybag(String userId, String[] commands) {
        ArrayList<CmdsResponseBody> bodys = new ArrayList<>();
        CmdsResponseBody body = new CmdsResponseBody();

        // 1.校验参数
        if ("".equals(userId) || !loginModel.isUserOnline(userId)) {
            body.getMessages().add("您还没有登陆，请先登录！");
        } else if (GuideUtil.userGuide.get(userId) == 1) {
            body.getMessages().add("您正在主界面中，如需进行游戏，请先进入游戏！");
        } else if (GuideUtil.userGuide.get(userId) == 2) {
            body.getMessages().add("您正在聊天房间中，如需进行游戏，请先退出房间！");
        } else if (GuideUtil.userGuide.get(userId) == 4) {
            body.getMessages().add("您正在商店界面，如需进行游戏，请先退出房间！");
        } else if (GuideUtil.userGuide.get(userId) == 5) {
            body.getMessages().add("您正在体力界面，如需进行游戏，请先退出房间！");
        } else if (commands.length > 2) {
            body.getMessages().add("请输入正确的命令格式！");
        } else if (commands.length == 2 && !"passCard".equals(commands[1])) {
            body.getMessages().add("你没有这种道具！");
        } else if (commands.length == 1) {
            // 2.1 mybag 查看背包
            body.getMessages().add("== == == == == == == == == 查看背包 == == == == == == == == == == == ==");
            Integer itemCount = getUserItems(userId);
            if (itemCount == null || itemCount == 0) {
                body.getMessages().add("== 游戏播报：你的背包空空如也~");
            } else {
                body.getMessages().add("== 游戏播报：<百无禁忌符> * " + itemCount + "  :使用方法：mybag passCard");
            }

        } else {

            // 2.2 mybag passCard 使用道具
            Integer itemCount = getUserItems(userId);
            if (itemCount == 0) {
                body.getMessages().add("== 游戏播报：你没有这种道具！");
            } else {
                // 2.2.1 道具减一
                minusOneUserItem(userId, itemCount - 1);
                // 2.2.2 跳到下一关
                PassfingerConfigBean level = passFingerCache.getLevelByUserId(userId);
                String newLevelNum = (Integer.parseInt(level.getLevelId()) + 1) + "";
                passFingerCache.addUserRecord(userId, newLevelNum);
                PassfingerConfigBean newLevel = passFingerCache.getLevelByNum(newLevelNum);

                // 3.设置返回数据
                dividingLine(body.getMessages());
                body.getMessages().add("== 游戏播报：使用道具 <百无禁忌符> ,怪物无视了你，你直接通过了当前关卡");
                dividingLine(body.getMessages());
                nextLevel(userId, body, newLevel);
            }
        }
        body.getUserIds().add(userId);
        GuideUtil.setUserGuide(body.getMessages(), userId);
        bodys.add(body);

        // 4.输出日志
        log.info("用户" + userId + "查看背包日志：" + bodys.toString());

        return bodys;
    }

    /**
     * 更新道具数
     *
     * @param userId
     * @param newitemCount
     */
    private void minusOneUserItem(String userId, int newitemCount) {
        String itemName = userId + "-pass";
        passFingerCache.addUserItems(itemName, newitemCount);
        redis.set(itemName, newitemCount + "");
        passFingerDao.saveUserItems(userId, newitemCount);
    }

    /**
     * 查看背包
     *
     * @param commands
     * @return
     */
    public List<CmdsResponseBody> back(String userId, String[] commands) {
        ArrayList<CmdsResponseBody> bodys = new ArrayList<>();
        CmdsResponseBody body = new CmdsResponseBody();

        // 1.校验参数
        if ("".equals(userId) || !loginModel.isUserOnline(userId)) {
            body.getMessages().add("您还没有登陆，请先登录！");
        } else if (GuideUtil.userGuide.get(userId) == 1) {
            body.getMessages().add("您正在主界面中，如需进行游戏，请先进入游戏！");
        } else if (GuideUtil.userGuide.get(userId) == 2) {
            body.getMessages().add("您正在聊天房间中，请使用quit命令退出！");
        } else if (commands.length != 1) {
            body.getMessages().add("请输入正确的命令格式！");
        } else {
            // 2.退出游戏 还原进度
            passFingerCache.addUserRecord(userId + "-passfinger", "1");
            GuideUtil.userGuide.put(userId, 1);

            // 3.返回数据
            dividingLine(body.getMessages());
            body.getMessages().add("== 返回主界面成功！ ==");
        }
        body.getUserIds().add(userId);
        GuideUtil.setUserGuide(body.getMessages(), userId);
        bodys.add(body);

        // 4.输出日志
        log.info("用户" + userId + "退出游戏日志：" + bodys.toString());

        return bodys;
    }

    /**
     * 获取道具数量
     *
     * @param userId
     * @return
     */
    private Integer getUserItems(String userId) {
        Integer itemCount = passFingerCache.getItemCountByName(userId + "-pass");
        if (itemCount == null || itemCount == 0) {
            String itemCountStr = redis.get(userId + "-pass");
            if (itemCountStr == null) {
                itemCount = passFingerDao.getUserItem(userId).getItemCount();
                redis.set(userId + "-pass", itemCount + "");
            } else {
                itemCount = Integer.parseInt(itemCountStr);
            }
        }
        return itemCount;
    }

    /**
     * 开始游戏的引导
     *
     * @param userId
     * @param body
     * @param level
     */
    private void gameStartGuide(String userId, CmdsResponseBody body, PassfingerConfigBean level) {
        body.getMessages().add("== == == == == == == == == 猜拳打关 == == == == == == == == == == == ==");
        body.getMessages().add("== 游戏播报：在这条路的终点埋藏着宝藏，前进吧，战胜途中的怪物，到达终点！！！ ");
        dropItemTenPercent(userId, body.getMessages());
        body.getMessages().add("== 游戏播报：你开始沿着路途前进，你来到：" + level.getLevelName());
        body.getMessages().add("== 游戏播报：一个怪物挡住了你的去路，它是：" + level.getMonsterName());
    }

    /**
     * 进行下一关
     *
     * @param userId
     * @param body
     * @param newLevel
     */
    private void nextLevel(String userId, CmdsResponseBody body, PassfingerConfigBean newLevel) {
        if (newLevel == null) {
            dividingLine(body.getMessages());
            dividingLine(body.getMessages());
            body.getMessages().add("== 游戏播报：你继续沿着路途前进，前面视线开阔，你终于抵达了终点！！");
            body.getMessages().add("== 游戏播报：恭喜你获得了《宝藏》！！！");
            body.getMessages().add("== 游戏播报：游戏结束，回到主界面 ");
            dividingLine(body.getMessages());
            GuideUtil.userGuide.put(userId, 1);
        } else {
            dropItemTenPercent(userId, body.getMessages());
            nextLevelGuide(body, newLevel);
        }
    }

    /**
     * 设置分割线
     *
     * @param msg
     */
    private void dividingLine(List<String> msg) {
        msg.add("== == == == == == == == == == == == == == == == == == == == == == == == == ==");
    }

    /**
     * 下一关存在的引导
     *
     * @param body
     * @param newLevel
     */
    private void nextLevelGuide(CmdsResponseBody body, PassfingerConfigBean newLevel) {
        body.getMessages().add("== 游戏播报：你继续沿着路途前进，你来到：" + newLevel.getLevelName());
        body.getMessages().add("== 游戏播报：一个怪物挡住了你的去路，它是：" + newLevel.getMonsterName());
    }

    /**
     * 随机获取怪物的猜拳值
     *
     * @return
     */
    private PassfingerCacheBean getMonsterFinger() {
        String finger = "";
        int randomNum = (int) (1 + Math.random() * (3 - 1 + 1));
        switch (randomNum) {
            case 1:
                finger = "shears";
                break;
            case 2:
                finger = "rock";
                break;
            case 3:
                finger = "paper";
                break;
            default:
                break;
        }
        return PassfingerCacheBean.getFingerGuessByName(finger);
    }

    /**
     * 、
     * 判断胜利者
     *
     * @param userId
     * @param fingerGuessCacheBean
     * @param existfingerGuess
     * @param body
     * @param levelNum
     */
    private void getWinner(String userId, PassfingerCacheBean fingerGuessCacheBean, PassfingerGroupCacheBean existfingerGuess, CmdsResponseBody body, String levelNum) {
        String monsterName = existfingerGuess.getPlayerOne();
        PassfingerCacheBean playerOneHand = existfingerGuess.getPlayerOneHand();
        int result = existfingerGuess.playerTwoJoinGame(userId, fingerGuessCacheBean);
        dividingLine(body.getMessages());
        body.getMessages().add("== 游戏播报：你发起猜拳攻击");
        body.getMessages().add("== 游戏播报：你本次出拳为：（" + fingerGuessCacheBean.getName() +
                "）  同时惊动了" + monsterName + "，怪物出拳为：（" + playerOneHand.getName() + "） ");

        switch (result) {
            case 0: {
                body.getMessages().add("== 游戏播报：游戏结果：平局 ");
                body.getMessages().add("== 尝试继续发起攻击吧！ ");
                GuideUtil.setUserGuide(body.getMessages(), userId);
                break;
            }
            case 1: {
                passFingerCache.setUserRecord(userId, "1");
                body.getMessages().add("== 游戏播报：游戏结果：很遗憾！" + monsterName + playerOneHand.getName() + "获得了胜利 ");
                body.getMessages().add("== 游戏播报：你不敌 " + monsterName + "，在途中倒下了");
                body.getMessages().add("== 游戏播报：游戏结束，回到起点 ！！！");
                // 猜拳保底机制
                passFingerCache.addUserGuaranteed(userId);

                List<CmdsResponseBody> bodys = passfinger(userId, new String[]{"passfinger"});
                body.getMessages().addAll(bodys.get(0).getMessages());
                break;
            }
            case -1: {
                body.getMessages().add("== 游戏播报：游戏结果：恭喜！你战胜了" + monsterName + "！！！");
                String newLevelNum = (Integer.parseInt(levelNum) + 1) + "";
                passFingerCache.addUserRecord(userId, newLevelNum);
                PassfingerConfigBean newLevel = passFingerCache.getLevelByNum(newLevelNum);
                nextLevel(userId, body, newLevel);
                GuideUtil.setUserGuide(body.getMessages(), userId);
                break;
            }
            default:
                System.out.println("出现异常");
        }
    }

    /**
     * 10%概率掉落打关必胜道具
     * (int) (1 + Math.random() * (最大值 - 最小值 + 1));
     *
     * @param userId
     * @param msg
     */
    private void dropItemTenPercent(String userId, List<String> msg) {
        int randomNum = (int) (1 + Math.random() * (10 - 1 + 1));
        if (randomNum == 1) {
            msg.add("== 游戏播报：咦！你在路边发现了一个闪闪发光的东西，过去把它捡了起来");
            msg.add("== 游戏播报：获得游戏道具 <百无禁忌符> * 1 (使用后可直接通行当前关卡，使用后消失)！！！");
            String itemName = userId + "-pass";
            Integer itemCount = getUserItems(userId);
            if (itemCount == null || itemCount == 0) {
                passFingerCache.addUserItems(itemName, 1);
                redis.set(itemName, "1");
                passFingerDao.saveUserItems(userId, 1);
            } else {
                passFingerCache.addUserItems(itemName, itemCount + 1);
                redis.set(itemName, (itemCount + 1) + "");
                passFingerDao.saveUserItems(userId, itemCount + 1);
            }
            log.info("随机数为：" + randomNum + "  本关掉落道具");
        } else {
            log.info("随机数为：" + randomNum + "  本关没有掉落道具");
        }
    }

}
