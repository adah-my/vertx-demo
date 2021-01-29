package telnet.util;


import telnet.passfinger.model.PassfingerModel;
import telnet.shop.dao.bean.ShopItemDaoBean;
import telnet.shop.model.ShopModel;
import telnet.strength.cache.StrengthCache;
import telnet.strength.cache.bean.StrengthCacheBean;

import java.util.HashMap;
import java.util.List;

/**
 * @author muyi
 * @description: 用户界面引导工具
 * @date 2020-10-28 09:58:13
 */
public class GuideUtil {

    public static HashMap<String, Integer> userGuide = new HashMap<>();

    /**
     * 根据用户所在的界面添加对应的引导
     *
     * @param msg
     * @param userId
     */
    public static void setUserGuide(List<String> msg, String userId) {
        Integer guideLevel;
        if ("".equals(userId)) {
            guideLevel = 0;
        } else if (!userGuide.containsKey(userId)) {
            guideLevel = 0;
        } else {
            guideLevel = userGuide.get(userId);
        }

        if (guideLevel == 0) {
            guideRegister(msg);
        } else if (guideLevel == 1) {
            guideLogin(msg);
        } else if (guideLevel == 2) {
            guideChat(msg);
        }else if (guideLevel == 3) {
            guidePassFinger(msg);
        }else if (guideLevel == 4) {
            itemShopGuide(msg,userId);
        }else if (guideLevel == 5) {
            strengthGuide(msg,userId);
        }

    }

    /**
     * 注册引导 0
     *
     * @param msg
     */
    public static void guideRegister(List<String> msg) {
        msg.add("== == == == == == == == == == == == == == == == == == == == == == == == == ==");
        msg.add("== 您可以继续以下操作：");
        msg.add("== 注册(没有账号注册一个)：register muyi 123456 ");
        msg.add("== 登录(登录你的账号)：login muyi 123456 ");
        msg.add("== == == == == == == == == == == == == == == == == == == == == == == == == ==");
    }

    /**
     * 主界面引导 1
     *
     * @param msg
     */
    public static void guideLogin(List<String> msg) { 
        msg.add("== == == == == == == == == == == == == == == == == == == == == == == == == ==");
        msg.add("== 您可以继续以下操作：");
        msg.add("== 查看(查看个人信息)：view  ");
        msg.add("== 私聊(私聊其他在线用户)：tell /用户 聊天内容 ");
        msg.add("== 进入房间(与房间所有用户聊天)：join 任意房间 ");
        msg.add("== 道具商店(每天出售珍贵道具)：shop ");
        msg.add("== 体力商店(可以购买体力)：strength ");
        msg.add("== 猜拳打关(与怪物猜拳闯关)：passfinger ");
        msg.add("== 登出(登出当前账号)：logout ");
        msg.add("== == == == == == == == == == == == == == == == == == == == == == == == == ==");
    }

    /**
     * 聊天引导 2
     *
     * @param msg
     */
    public static void guideChat(List<String> msg) {
        msg.add("== == == == == == == == == == == == == == == == == == == == == == == == == ==");
        msg.add("== 您可以继续以下操作：");
        msg.add("== 私聊(私聊其他在线用户)：tell /user content ");
        msg.add("== 公聊(与房间所有用户聊天)：talk content ");
        msg.add("== 房间小游戏：输入命令了解详情吧");
        msg.add("== 目前可玩小游戏：finger(猜拳)");
        msg.add("== 退出(退出当前房间)：quit ");
        msg.add("== == == == == == == == == == == == == == == == == == == == == == == == == ==");
    }

    /**
     * 猜拳游戏引导  3
     *
     * @param msg
     */
    private static void guidePassFinger(List<String> msg) {
        msg.add("== == == == == == == == == == == == == == == == == == == == == == == == == ==");
        msg.add("== 攻击方式： 剪刀(shears) 石头(rock) 布(paper) ");
        msg.add("== 发起攻击：例：atk rock (向怪物发出猜拳攻击，且您本次出拳为石头) ");
        msg.add("== 记录存档：例：save (记录此次存档，注：上次存档将被覆盖) ");
        msg.add("== 读取存档：例：load (读取存档上次) ");
        msg.add("== 查看背包：例：mybag (查看背包中的道具) ");
        msg.add("== 退出游戏：例：back (退出当前游戏) ");
        msg.add("== == == == == == == == == == == == == == == == == == == == == == == == == ==");
    }

    /**
     * 商店引导 4
     *
     * @param msg
     */
    private static void itemShopGuide(List<String> msg, String userId) {
        ShopItemDaoBean shopItemDaoBean = ShopModel.getInstance().getShopItemByUserId(userId);
        int bagItemsCount = PassfingerModel.getInstance().getItemsCount(userId);


        msg.add("== == == == == == == == == 道具商店 == == == == == == == == == == == == == ==");
        msg.add("== 持有道具：《百无禁忌符》 * "+ bagItemsCount);
        msg.add("== 每天凌晨，这里都会刷新一些珍贵道具哦！！！ 今天的道具：");
        if (shopItemDaoBean.getItemCount() == 0){
            msg.add("== 今天的道具已经全部出售，明天再来吧！！");
        }else{
            msg.add("== 《百无禁忌符》 * " + shopItemDaoBean.getItemCount()+"    购买方式：例：buy passCard 1（购买一张符箓）");
        }
        msg.add("== 返回主界面：back");
        msg.add("== == == == == == == == == == == == == == == == == == == == == == == == == ==");
    }

    /**
     * 体力引导 5
     *
     * @param msg
     */
    private static void strengthGuide(List<String> msg, String userId) {
        StrengthCacheBean userStrength = StrengthCache.getInstance().getUserStrengthById(userId);
        msg.add("== == == == == == == == == 体力界面 == == == == == == == == == == == == == ==");
        msg.add("== 你的体力："+userStrength.getStrengthCount()+"    (每五分钟回复1点体力，体力回复上限：100)");
        if (userStrength.getStrengthPurchase() >= 10){
            msg.add("== 今天购买的体力已达上限");
        }else {
            msg.add("== 体力购买：60体  单价："+(userStrength.getStrengthPurchase()+1)+"元宝        购买方式：pay");
        }
        msg.add("== 返回主界面：back ");
        msg.add("== == == == == == == == == == == == == == == == == == == == == == == == == ==");
    }

}
