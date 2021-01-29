package telnet.passfinger.config.bean;


/**
 * @author muyi
 * @description:
 * @date 2020-11-05 10:47:01
 */
public class PassfingerConfigBean {
    /**
     * 关卡id
     */
    private String levelId;
    /**
     * 关卡名
     */
    private String levelName;
    /**
     * 关卡内的怪物
     */
    private String monsterName;

    public PassfingerConfigBean() {
    }

    public PassfingerConfigBean(String levelId, String levelName, String monsterName) {
        this.levelId = levelId;
        this.levelName = levelName;
        this.monsterName = monsterName;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getMonsterName() {
        return monsterName;
    }

    public void setMonsterName(String monsterName) {
        this.monsterName = monsterName;
    }

    @Override
    public String toString() {
        return "PassFingerConfigBean{" +
                "levelId='" + levelId + '\'' +
                ", levelName='" + levelName + '\'' +
                ", monsterName='" + monsterName + '\'' +
                '}';
    }
}

