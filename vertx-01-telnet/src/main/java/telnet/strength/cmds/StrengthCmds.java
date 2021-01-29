package telnet.strength.cmds;

import telnet.core.common.CmdsResponseBody;
import telnet.login.model.LoginModel;
import telnet.strength.cache.StrengthCache;
import telnet.strength.cache.bean.StrengthCacheBean;
import telnet.strength.dao.StrengthDao;
import telnet.util.GuideUtil;
import telnet.util.RedisUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author muyi
 * @description:
 * @date 2020-11-05 18:36:35
 */
public class StrengthCmds {

    LoginModel loginModel;
    RedisUtil redis;
    StrengthCache strengthCache;
    StrengthDao strengthDao;

    /**
     * 日志
     */
    public static final Logger log = Logger.getLogger(StrengthCmds.class.getName());

    public StrengthCmds() {
        loginModel = LoginModel.getInstance();
        redis = RedisUtil.getInstance();
        strengthCache = StrengthCache.getInstance();
        strengthDao = StrengthDao.getInstance();
    }

    /**
     * 进入体力界面
     *
     * @param commands
     * @return
     */
    public List<CmdsResponseBody> strength(String userId, String[] commands) {
        ArrayList<CmdsResponseBody> bodys = new ArrayList<>();
        CmdsResponseBody body = new CmdsResponseBody();

        // 1.校验参数
        if ("".equals(userId) || !loginModel.isUserOnline(userId)) {
            body.getMessages().add("您还没有登陆，请先登录！");
        } else if (GuideUtil.userGuide.get(userId) == 2) {
            body.getMessages().add("您正在聊天房间中，如需进入体力界面，请先退出！");
        } else if (GuideUtil.userGuide.get(userId) == 3) {
            body.getMessages().add("您正在游戏中，如需入体力界面，请先退出！");
        } else if (GuideUtil.userGuide.get(userId) == 4) {
            body.getMessages().add("您正在商店中，如需入体力界面，请先退出！");
        } else if (commands.length != 1) {
            body.getMessages().add("请输入正确的命令格式！");
        } else {
            // 2.进入体力界面
            GuideUtil.userGuide.put(userId, 5);
            String userStrength = redis.hget("userStrength", userId);
            String userStrengthPurchase = redis.hget("userStrengthPurchase", userId);
            StrengthCacheBean strengthCacheBean;
            if (userStrength == null) {
                strengthCacheBean = strengthDao.findUserStrengthById(userId);
                redis.hset("userStrength", userId, strengthCacheBean.getStrengthCount() + "");
                redis.hset("userStrengthPurchase", userId, strengthCacheBean.getStrengthPurchase() + "");
            } else {
                strengthCacheBean = new StrengthCacheBean(userId, Integer.parseInt(userStrength), Integer.parseInt(userStrengthPurchase));
            }
            strengthCache.updateUserStrength(userId, strengthCacheBean);

            // 3.返回数据
        }
        body.getUserIds().add(userId);
        GuideUtil.setUserGuide(body.getMessages(), userId);
        bodys.add(body);

        // 4.输出日志
        log.info("用户" + userId + "进入体力界面日志：" + bodys.toString());

        return bodys;
    }

    /**
     * 进入体力界面
     *
     * @param commands
     * @return
     */
    public List<CmdsResponseBody> pay(String userId, String[] commands) {
        ArrayList<CmdsResponseBody> bodys = new ArrayList<>();
        CmdsResponseBody body = new CmdsResponseBody();

        // 1.校验参数
        if ("".equals(userId) || !loginModel.isUserOnline(userId)) {
            body.getMessages().add("您还没有登陆，请先登录！");
        } else if (GuideUtil.userGuide.get(userId) == 1) {
            body.getMessages().add("请先进入体力界面！");
        } else if (GuideUtil.userGuide.get(userId) == 2) {
            body.getMessages().add("您正在聊天房间中，如需购买体力，请先退出！");
        } else if (GuideUtil.userGuide.get(userId) == 3) {
            body.getMessages().add("您正在游戏中，如需购买体力，请先退出！");
        } else if (GuideUtil.userGuide.get(userId) == 4) {
            body.getMessages().add("您正在商店中，如需购买体力，请先退出！");
        } else if (commands.length != 1) {
            body.getMessages().add("请输入正确的命令格式！");
        } else {
            // 2.进入体力界面
            StrengthCacheBean userStrength = strengthCache.getUserStrengthById(userId);
            if (userStrength.getStrengthPurchase() >= 10) {
                body.getMessages().add("今天购买的体力已达上限，无法购买！");
            } else {
                userStrength.setStrengthCount(userStrength.getStrengthCount() + 60);
                userStrength.setStrengthPurchase(userStrength.getStrengthPurchase() + 1);
                strengthCache.updateUserStrength(userId, userStrength);
                redis.hset("userStrength", userId, userStrength.getStrengthCount() + "");
                redis.hset("userStrengthPurchase", userId, userStrength.getStrengthPurchase() + "");
                strengthDao.saveUserStrength(userStrength);
                body.getMessages().add("购买成功！体力 +60");
            }
            // 3.返回数据
        }
        body.getUserIds().add(userId);
        GuideUtil.setUserGuide(body.getMessages(), userId);
        bodys.add(body);

        // 4.输出日志
        log.info("用户" + userId + "进入体力界面日志：" + bodys.toString());

        return bodys;
    }
}
