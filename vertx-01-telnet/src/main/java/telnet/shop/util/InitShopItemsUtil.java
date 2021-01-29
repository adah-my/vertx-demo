package telnet.shop.util;

import telnet.shop.cache.ShopCache;
import telnet.shop.dao.ShopDao;
import telnet.util.RedisUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author muyi
 * @description:
 * @date 2020-11-05 21:25:13
 */
public class InitShopItemsUtil {

    ShopCache shopCache;
    ShopDao shopDao;
    RedisUtil redis;
    

    public InitShopItemsUtil(){
        shopCache = ShopCache.getInstance();
        shopDao = ShopDao.getInstance();
        redis = RedisUtil.getInstance();
    }



    public static void main(String[] args) {
        new InitShopItemsUtil().executeAtMidnightPerDay();
    }


    /**
     * 每天凌晨0点执行一次 每天定时安排任务进行执行
     */
    public void executeAtMidnightPerDay() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        long oneDay = 24 * 60 * 60 * 1000;
        // 执行任务的时间
        long initDelay = getTimeMillis("16:00:00") - System.currentTimeMillis();
        initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;

        executor.scheduleAtFixedRate(() -> {

            // 0点更新 内存 redis 数据库中所有的商店道具
            System.out.println("执行定时任务");
            shopCache.initAllUserItems();
            initRedisShopItems();
            shopDao.delAllShopItems();

        }, initDelay, oneDay, TimeUnit.MILLISECONDS);
    }

    /**
     * 初始化redis中商店道具
     */
    private void initRedisShopItems() {
        Map<String, String> shopItems = redis.hgetall("shopItems");
        Iterator<Map.Entry<String, String>> iterator = shopItems.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> entry = iterator.next();
            if (!"10".equals(entry.getValue())){
                redis.hset("shopItems", entry.getKey(), "10");
            }
        }
    }

    /**
     * 获取指定时间对应的毫秒数
     *
     * @param time
     *            "HH:mm:ss"
     * @return
     */
    private long getTimeMillis(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            return curDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }



}

