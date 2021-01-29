package telnet.strength.util;

import telnet.strength.cache.StrengthCache;
import telnet.strength.cache.bean.StrengthCacheBean;
import telnet.strength.dao.StrengthDao;
import telnet.util.JfinalUtil;
import telnet.util.RedisUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author muyi
 * @description:
 * @date 2020-11-06 17:40:36
 */
public class InitStrengthParchaseUtil {

    StrengthCache strengthCache;
    StrengthDao strengthDao;
    RedisUtil redis;


    public InitStrengthParchaseUtil(){
        strengthCache = StrengthCache.getInstance();
        strengthDao = StrengthDao.getInstance();
        redis = RedisUtil.getInstance();
    }


    public static void main(String[] args) {
        JfinalUtil.getInstance().loadPlugin();
        new ReplyStrengthUtil().executeFiveMinutes();
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

            // 0点更新 体力购买次数
            System.out.println("0点刷新用户购买的体力次数");
            strengthCache.initStrengthParchase();
            initRedisStrengthPurchase();
            initDbStrengthPurchase();



        }, initDelay, oneDay, TimeUnit.MILLISECONDS);
    }

    /**
     * 更新数据库中体力购买次数
     */
    private void initDbStrengthPurchase() {
        List<StrengthCacheBean> allUserStrength = strengthDao.findAllUserStrength();
        for (StrengthCacheBean bean : allUserStrength){
            int strengthPurchase = bean.getStrengthPurchase();
            if (strengthPurchase != 0){
                bean.setStrengthPurchase(0);
                strengthDao.saveUserStrength(bean);
            }
        }
    }

    /**
     * 初始化redis中商店道具
     */
    private void initRedisStrengthPurchase() {
        Map<String, String> shopItems = redis.hgetall("userStrengthPurchase");
        Iterator<Map.Entry<String, String>> iterator = shopItems.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> entry = iterator.next();
            if (!"0".equals(entry.getValue())){
                redis.hset("userStrengthPurchase", entry.getKey(), "0");
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
