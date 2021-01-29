package telnet.finger.cache.bean;

/**
 * @author muyi
 * @description:
 * @date 2020-10-31 10:31:03
 */
public enum FingerGuessCacheBean {
    /**
     * 剪刀石头布
     */
    SCISSORS("剪刀(shears)",1),ROCK("石头(rock)",2),PAPER("布(paper)",3);

    private String name;
    private int value;


    private FingerGuessCacheBean(String name, int value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    /**
     * 根据名字获取fingerGuess
     * @param name
     * @return
     */
    public static FingerGuessCacheBean getFingerGuessByName(String name){
        FingerGuessCacheBean fingerGuessCacheBean;
        if (name == null){
            fingerGuessCacheBean = null;
        } else if ("shears".equals(name)){
            fingerGuessCacheBean = SCISSORS;
        } else if ("rock".equals(name)){
            fingerGuessCacheBean = ROCK;
        } else if ("paper".equals(name)){
            fingerGuessCacheBean = PAPER;
        } else{
            fingerGuessCacheBean = null;
        }
        return fingerGuessCacheBean;
    }
}
