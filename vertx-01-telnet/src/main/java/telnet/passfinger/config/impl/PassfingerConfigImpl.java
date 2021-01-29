package telnet.passfinger.config.impl;

import telnet.passfinger.config.PassfingerConfig;
import telnet.passfinger.config.bean.PassfingerConfigBean;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author muyi
 * @description:
 * @date 2020-11-05 10:46:14
 */
public class PassfingerConfigImpl implements PassfingerConfig {
    private static String csvFile = "passlevels.csv";


    private volatile static PassfingerConfig instance;

    private PassfingerConfigImpl(){
        
    }
    /**
     * @return PassFingerConfig
     */
    public static PassfingerConfig getInstance() {
        if (instance == null) {
            synchronized (PassfingerConfigImpl.class) {
                if (instance == null) {
                    instance = new PassfingerConfigImpl();
                }
            }
        }
        return instance;
    }

    /**
     * 读取配置文件中的关卡
     * @return
     */
    @Override
    public ConcurrentHashMap<String, PassfingerConfigBean> getLevelsFromFile() {

        ConcurrentHashMap<String, PassfingerConfigBean> levels = new ConcurrentHashMap<String, PassfingerConfigBean>();

        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // 读取每一行的关卡数据
                String[] level = line.split(cvsSplitBy);

                PassfingerConfigBean configBean = new PassfingerConfigBean(level[0],level[1],level[2]);

                levels.put(level[0],configBean);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return levels;
    }

//    public static void main(String[] args) {
//
//        String[] levels = new String[6];
//        levels[0] = "1,一面道中,怪物1";
//        levels[1] = "2,一面道中,怪物2";
//        levels[2] = "3,一面道中,怪物3";
//        levels[3] = "4,一面道中,怪物4";
//        levels[4] = "5,一面道中,怪物5";
//        levels[5] = "6,一面道中,怪物6";
//
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {
//            for (int i = 0; i < levels.length; i++) {
//                bw.write(levels[i]);
//                bw.write("\r\n");
//                bw.flush();
//            }
//            System.out.println("写入完毕~");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
