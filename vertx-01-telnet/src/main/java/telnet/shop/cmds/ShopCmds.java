package telnet.shop.cmds;

import telnet.core.common.CmdsResponseBody;
import telnet.login.model.LoginModel;
import telnet.passfinger.model.PassfingerModel;
import telnet.shop.cache.ShopCache;
import telnet.shop.dao.ShopDao;
import telnet.shop.dao.bean.ShopItemDaoBean;
import telnet.util.GuideUtil;
import telnet.util.RedisUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author muyi
 * @description:
 * @date 2020-11-05 18:32:00
 */
public class ShopCmds {

    LoginModel loginModel;
    RedisUtil redis;
    ShopDao shopDao;
    ShopCache shopCache;
    PassfingerModel passfingerModel;
    /**
     * 日志
     */
    public static final Logger log = Logger.getLogger(ShopCmds.class.getName());

    public ShopCmds() {
        loginModel = LoginModel.getInstance();
        redis = RedisUtil.getInstance();
        shopDao = ShopDao.getInstance();
        shopCache = ShopCache.getInstance();
        passfingerModel = PassfingerModel.getInstance();
    }

    /**
     * 进入商店
     *
     * @param commands
     * @return
     */
    public List<CmdsResponseBody> shop(String userId, String[] commands) {
        ArrayList<CmdsResponseBody> bodys = new ArrayList<>();
        CmdsResponseBody body = new CmdsResponseBody();

        // 1.校验参数
        if ("".equals(userId) || !loginModel.isUserOnline(userId)) {
            body.getMessages().add("您还没有登陆，请先登录！");
        }else if (GuideUtil.userGuide.get(userId) == 2) {
            body.getMessages().add("您正在聊天房间中，如需进入商店，请先退出房间！");
        } else if (GuideUtil.userGuide.get(userId) == 3) {
            body.getMessages().add("您正在游戏中，如需进入商店，请先退出游戏！");
        }  else if (GuideUtil.userGuide.get(userId) == 5) {
            body.getMessages().add("您正在体力界面，如需进入商店，请先退出界面！");
        }   else if (commands.length != 1) {
            body.getMessages().add("请输入正确的命令格式！");
        } else {
            // 2.进入商店
            GuideUtil.userGuide.put(userId,4);
            String shopItemsCount = redis.hget("shopItems", userId + "-shop-pass");
            ShopItemDaoBean shopItemDaoBean;
            if (shopItemsCount == null){
                shopItemDaoBean = shopDao.findShopItemsByUserId(userId);
                if (shopItemDaoBean == null){
                    shopItemDaoBean = new ShopItemDaoBean(userId+"-shop-pass",10);
                } else {
                    redis.hset("shopItems", shopItemDaoBean.getItemName(),shopItemDaoBean.getItemCount()+"");
                }
            } else {
                shopItemDaoBean = new ShopItemDaoBean(userId+"-shop-pass", Integer.parseInt(shopItemsCount));
            }
            shopCache.saveUserShopItems(shopItemDaoBean.getItemName(),shopItemDaoBean.getItemCount());

            // 3.返回数据
        }
        body.getUserIds().add(userId);
        GuideUtil.setUserGuide(body.getMessages(), userId);
        bodys.add(body);

        // 4.输出日志
        log.info("用户" + userId + "商店日志：" + bodys.toString());

        return bodys;
    }

    /**
     * 进入商店
     *
     * @param commands
     * @return
     */
    public List<CmdsResponseBody> buy(String userId, String[] commands) {
        ArrayList<CmdsResponseBody> bodys = new ArrayList<>();
        CmdsResponseBody body = new CmdsResponseBody();

        // 1.校验参数
        if ("".equals(userId) || !loginModel.isUserOnline(userId)) {
            body.getMessages().add("您还没有登陆，请先登录！" );
        }else if (GuideUtil.userGuide.get(userId) == 1) {
            body.getMessages().add("请先进入商店！");
        } else if (GuideUtil.userGuide.get(userId) == 2) {
            body.getMessages().add("您正在聊天房间中，如需购买道具，请先退出！");
        } else if (GuideUtil.userGuide.get(userId) == 3) {
            body.getMessages().add("您正在游戏中，如需购买道具，请先退出！");
        }  else if (GuideUtil.userGuide.get(userId) == 5) {
            body.getMessages().add("您正在体力界面，如需购买道具，请先退出！");
        } else if (commands.length != 3 || !"passCard".equals(commands[1])) {
            body.getMessages().add("请输入正确的命令格式！");
        } else {
            // 2.进入商店
            boolean flag;
            String itemName = userId + "-shop-pass";
            int buyCount = 0;
            int newItemCount = 0;
            try {
                buyCount = Integer.parseInt(commands[2]);
                int itemCount = shopCache.getItemCountByName(itemName);
                newItemCount = itemCount - buyCount;
                flag = false;
                if (newItemCount < 0){
                    flag = true;
                }
            }catch (Exception e){
                flag = true;
            }
            if (flag){
                body.getMessages().add("请输入合适的值！");
            } else {
                shopCache.saveUserShopItems(itemName,newItemCount);
                redis.hset("shopItems",itemName, newItemCount+"");
                shopDao.saveItemCount(userId ,new ShopItemDaoBean(itemName, newItemCount));
                passfingerModel.addUserItems(userId,buyCount);
                // 3.返回数据
                body.getMessages().add("成功购买：《百无禁忌符》 * "+buyCount);
            }
        }
        body.getUserIds().add(userId);
        GuideUtil.setUserGuide(body.getMessages(), userId);
        bodys.add(body);

        // 4.输出日志
        log.info("用户" + userId + "商店日志：" + bodys.toString());

        return bodys;
    }

}
