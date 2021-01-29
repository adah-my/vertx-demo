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
 * @date 2020-11-06 16:21:39
 */
public class ReplyStrengthUtil {

    StrengthCache strengthCache;
    StrengthDao strengthDao;
    RedisUtil redis;


    public ReplyStrengthUtil(){
        strengthCache = StrengthCache.getInstance();
        strengthDao = StrengthDao.getInstance();
        redis = RedisUtil.getInstance();
    }


    public static void main(String[] args) {
        JfinalUtil.getInstance().loadPlugin();
        new ReplyStrengthUtil().executeFiveMinutes();
    }


    /**
     * 每5分钟执行一次
     */
    public void executeFiveMinutes() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        long fiveMinutes = 5 * 60 * 1000;
        long initDelay = getTimeMillis("14:00:00") - System.currentTimeMillis();
        initDelay = initDelay > 0 ? initDelay : fiveMinutes + initDelay;

        executor.scheduleAtFixedRate(() -> {

            // 每5分钟执行一次 内存 redis 数据库中所有的体力
            System.out.println("自动执行回体~");
            strengthCache.replayUserStrength();
            replayRedisStrength();
            replayDbStrength();



        }, initDelay, fiveMinutes, TimeUnit.MILLISECONDS);
    }

    /**
     * 数据库回体
     */
    private void replayDbStrength() {
        List<StrengthCacheBean> allUserStrength = strengthDao.findAllUserStrength();
        for (StrengthCacheBean bean : allUserStrength){
            int strengthCount = bean.getStrengthCount();
            if (strengthCount < 100){
                bean.setStrengthCount(strengthCount+1);
                strengthDao.saveUserStrength(bean);
            }
        }
    }

    /**
     * redis记录增加体力
     */
    private void replayRedisStrength() {
        Map<String, String> shopItems = redis.hgetall("userStrength");
        Iterator<Map.Entry<String, String>> iterator = shopItems.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> entry = iterator.next();
            int userStrenth = Integer.parseInt(entry.getValue());
            if (userStrenth < 100){
                redis.hset("userStrength", entry.getKey(), (userStrenth+1)+"");
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
