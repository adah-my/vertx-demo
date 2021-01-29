package telnet.finger.cache.bean;

/**
 * @author muyi
 * @description: 猜拳游戏组
 * @date 2020-10-31 10:27:29
 */
public class FingerGuessGroupCacheBean {

    /**
     * 玩家1
     */
    private String playerOne;
    /**
     * 玩家1出拳
     */
    private FingerGuessCacheBean playerOneHand;

    /**
     * 玩家2
     */
    private String playerTwo;
    /**
     * 玩家2出拳
     */
    private FingerGuessCacheBean playerTwoHand;

    /**
     * 玩家1发起一局游戏
     *
     * @param playerOne
     * @param playerOneHand
     */
    public FingerGuessGroupCacheBean(String playerOne, FingerGuessCacheBean playerOneHand) {
        this.playerOne = playerOne;
        this.playerOneHand = playerOneHand;
    }

    /**
     * 玩家2加入游戏
     * @param playerTwo
     * @param playerTwoHand
     * @return
     */
    public int playerTwoJoinGame(String playerTwo, FingerGuessCacheBean playerTwoHand) {
        this.playerTwo = playerTwo;
        this.playerTwoHand = playerTwoHand;

        return getResult();
    }

    /**
     * 得到赢家：0为平局，大于0为玩家1胜利，小于0为玩家2胜利 5为异常
     * @return
     */
    private int getResult() {
        int result = 5;
        if (playerOne == null || "".equals(playerOne)) {
            System.out.println("本局猜拳游戏玩家1信息异常");
        } else if (playerTwo == null || "".equals(playerTwo)) {
            System.out.println("本局猜拳游戏玩家2信息异常");
        } else if (playerOneHand == null) {
            System.out.println("本局猜拳游戏玩家1出拳异常");
        } else if (playerTwoHand == null) {
            System.out.println("本局猜拳游戏玩家2出拳异常");
        } else {
            switch (playerOneHand.getValue()-playerTwoHand.getValue()){
                case 0:
                    result = 0; break;
                case 1:
                    result = 1;break;
                case -1:
                    result = -1;break;
                case -2:
                    result = 1; break;
                case 2:
                    result = -1;break;
                default:break;
            }

        }
        return result;
    }

    public String getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(String playerOne) {
        this.playerOne = playerOne;
    }

    public FingerGuessCacheBean getPlayerOneHand() {
        return playerOneHand;
    }

    public void setPlayerOneHand(FingerGuessCacheBean playerOneHand) {
        this.playerOneHand = playerOneHand;
    }

    public String getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(String playerTwo) {
        this.playerTwo = playerTwo;
    }

    public FingerGuessCacheBean getPlayerTwoHand() {
        return playerTwoHand;
    }

    public void setPlayerTwoHand(FingerGuessCacheBean playerTwoHand) {
        this.playerTwoHand = playerTwoHand;
    }
}
