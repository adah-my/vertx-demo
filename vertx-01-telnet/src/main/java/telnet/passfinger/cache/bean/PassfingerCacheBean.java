package telnet.passfinger.cache.bean;

/**
 * @author muyi
 * @description:
 * @date 2020-10-31 10:31:03
 */
public enum PassfingerCacheBean {
    /**
     * 剪刀石头布
     */
    SCISSORS("剪刀(shears)",1),ROCK("石头(rock)",2),PAPER("布(paper)",3);

    private String name;
    private int value;


    private PassfingerCacheBean(String name, int value){
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
    public static PassfingerCacheBean getFingerGuessByName(String name){
        PassfingerCacheBean fingerGuessCacheBean;
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

    /**
     * 根据值获取赢家
     * @param bean
     * @return
     */
    public static PassfingerCacheBean getWinnerFinger(PassfingerCacheBean bean){

        PassfingerCacheBean winnerBean;
        if (bean.value == 1){
            winnerBean = ROCK;
        }else if (bean.value == 2){
            winnerBean = PAPER;
        }else {
            winnerBean = SCISSORS;
        }
        return winnerBean;
    }

    /**
     * 根据值获取输家家
     * @param bean
     * @return
     */
    public static PassfingerCacheBean getLoseFinger(PassfingerCacheBean bean){

        PassfingerCacheBean lostBean;
        if (bean.value == 1){
            lostBean = PAPER;
        }else if (bean.value == 2){
            lostBean = SCISSORS;
        }else {
            lostBean = ROCK;
        }
        return lostBean;
    }

}
