package telnet.finger.cmds;

import telnet.chat.cache.bean.ChatRoomCacheBean;
import telnet.chat.model.ChatModel;
import telnet.core.common.CmdsResponseBody;
import telnet.finger.cache.FingerCache;
import telnet.finger.cache.bean.FingerGuessCacheBean;
import telnet.finger.cache.bean.FingerGuessGroupCacheBean;
import telnet.login.model.LoginModel;
import telnet.util.GuideUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author muyi
 * @description:
 * @date 2020-10-31 11:31:35
 */
public class FingerCmds {

    private FingerCache fingerCache;
    private LoginModel loginModel;
    private ChatModel chatModel;
    /**
     * 日志
     */
    public static final Logger log = Logger.getLogger(FingerCmds.class.getName());

    public FingerCmds() {
        loginModel = LoginModel.getInstance();
        chatModel = ChatModel.getInstance();
        fingerCache = FingerCache.getInstance();
    }

    /**
     * 发起猜拳游戏
     *
     * @param userId
     * @param commands
     * @return
     */
    public List<CmdsResponseBody> finger(String userId, String[] commands) {
        ArrayList<CmdsResponseBody> bodys = new ArrayList<>();
        CmdsResponseBody body = new CmdsResponseBody();

        // 1.参数校验
        if ("".equals(userId) || !loginModel.isUserOnline(userId)) {
            body.getMessages().add("您还没有登录，请先登录！");
        } else if (!chatModel.isUserChating(userId)) {
            body.getMessages().add("您还没有加入房间，请先加入房间！");
        } else if (commands.length == 1){
            fingerGuide(body.getMessages());
        }else if (commands.length != 2) {
            body.getMessages().add("请输入正确的猜拳格式！");
            fingerGuide(body.getMessages());
        } else if (FingerGuessCacheBean.getFingerGuessByName(commands[1]) == null) {
            body.getMessages().add("您输入的猜拳值有误，请重新输入！");
            fingerGuide(body.getMessages());
        } else if (isUserExistFingerGuess(userId)) {
            body.getMessages().add("您已在此房间发起了猜拳游戏，无法重复发起！");
            fingerGuide(body.getMessages());
        } else {

            // 2.创建新的猜拳游戏
            FingerGuessCacheBean fingerGuessCacheBean = FingerGuessCacheBean.getFingerGuessByName(commands[1]);
            // 2.1 获取用户房间
            ChatRoomCacheBean chatRoomCacheBean = chatModel.getChatRoomByUserId(userId);
            CmdsResponseBody chatBody = new CmdsResponseBody();
            // 2.2 对房间所有用户发出通知
            chatBody.getUserIds().addAll(chatRoomCacheBean.getChatUsers());
            // 2.3 保存新创建的游戏
            FingerGuessGroupCacheBean fingerGuessGroupCacheBean = new FingerGuessGroupCacheBean(userId, fingerGuessCacheBean);
            String chatroomUser = chatRoomCacheBean.getChatRoomName() + "-finger-" + userId;
            fingerCache.addFingerGuessGroup(chatroomUser, fingerGuessGroupCacheBean);

            // 3.设置返回数据
            body.getMessages().add("猜拳游戏发起成功，正在等待其他玩家响应！");
            fingerNotice(userId, chatBody);
            bodys.add(chatBody);

        }
        body.getUserIds().add(userId);
        GuideUtil.setUserGuide(body.getMessages(), userId);
        bodys.add(body);

        // 4.输出日志
        log.info("用户" + userId + "发起猜拳日志：" + bodys.toString());

        return bodys;
    }

    /**
     * 响应猜拳游戏
     *
     * @param userId
     * @param commands
     * @return
     */
    public List<CmdsResponseBody> refinger(String userId, String[] commands) {
        ArrayList<CmdsResponseBody> bodys = new ArrayList<>();
        CmdsResponseBody body = new CmdsResponseBody();

        // 1.参数校验
        if ("".equals(userId) || !loginModel.isUserOnline(userId)) {
            body.getMessages().add("您还没有登录，请先登录！");
        } else if (!chatModel.isUserChating(userId)) {
            body.getMessages().add("您还没有加入房间，请先加入房间！");
        } else if (commands.length >= 4 || commands.length <= 1) {
            fingerGuide(body.getMessages());
            body.getMessages().add("== 您输入的命令格式有误，请重新输入！ ==");
        } else if (commands.length == 2 && FingerGuessCacheBean.getFingerGuessByName(commands[1]) == null) {
            fingerGuide(body.getMessages());
            body.getMessages().add("== 您输入的猜拳值有误，请重新输入！ ==");
        } else if (commands.length == 3 && FingerGuessCacheBean.getFingerGuessByName(commands[2]) == null) {
            fingerGuide(body.getMessages());
            body.getMessages().add("== 您输入的猜拳值有误，请重新输入！ ==");
        } else if (commands.length == 3 && userId.equals(commands[1])) {
            fingerGuide(body.getMessages());
            body.getMessages().add("== 无法参加自己发起的猜拳游戏！ ==");
        } else {

            // 两种情况：refinger rock (直接响应游戏)或 refinger aaa rock(响应指定用户的游戏)
            // 2.1 根据名字获取猜拳值，获取到剪刀石头布实体
            FingerGuessCacheBean fingerGuessCacheBean;
            String targetUserId = "";
            if (commands.length == 2) {
                fingerGuessCacheBean = FingerGuessCacheBean.getFingerGuessByName(commands[1]);
            } else {
                fingerGuessCacheBean = FingerGuessCacheBean.getFingerGuessByName(commands[2]);
                targetUserId = commands[1];
            }

            // 2.2 获取已发布的游戏
            ChatRoomCacheBean chatRoomCacheBean = chatModel.getChatRoomByUserId(userId);
            FingerGuessGroupCacheBean existfingerGuess;
            if (commands.length == 2) {
                existfingerGuess = fingerCache.getOneFingerGuessGroup(chatRoomCacheBean.getChatRoomName());
            } else {
                existfingerGuess = fingerCache.getFingerGuessGroupByChatroomUser(chatRoomCacheBean.getChatRoomName() + "-finger-" + targetUserId);
            }
            // 2.3 判断获得的游戏是否存在，是否为自己所发布
            if (existfingerGuess == null) {
                // 不存在目标猜拳游戏
                body.getMessages().add("房间内不存在用户发起的猜拳游戏！");
                body.getMessages().add("== 使用finger来发起新游戏吧！ ==");
            } else if (userId.equals(existfingerGuess.getPlayerOne())) {
                // 无法参加自己发起的猜拳游戏
                body.getMessages().add("无法参加由自己发起的猜拳游戏！");
            } else {
                // 游戏无误
                CmdsResponseBody chatBody = new CmdsResponseBody();
                chatBody.getUserIds().addAll(chatRoomCacheBean.getChatUsers());
                // 2.4 参加游戏，判断赢家
                getWinner(userId, fingerGuessCacheBean, existfingerGuess, chatBody);
                // 2.5游戏结束，移除这局猜拳游戏
                fingerCache.delFingerGuessGroupByChatroomUser(chatRoomCacheBean.getChatRoomName() + "-finger-" + existfingerGuess.getPlayerOne());

                // 3.设置返回数据
                bodys.add(chatBody);
            }
        }
        body.getUserIds().add(userId);
        GuideUtil.setUserGuide(body.getMessages(), userId);
        bodys.add(body);

        // 4.输出日志
        log.info("用户" + userId + "响应猜拳日志：" + bodys.toString());

        return bodys;
    }

    /**
     * 判断用户是否已经发起过猜拳游戏
     *
     * @param userId
     * @return
     */
    private boolean isUserExistFingerGuess(String userId) {
        ChatRoomCacheBean chatRoom = chatModel.getChatRoomByUserId(userId);
        String chatroomUser = chatRoom.getChatRoomName() + "-finger-" + userId;
        FingerGuessGroupCacheBean existfingerGuess = fingerCache.getFingerGuessGroupByChatroomUser(chatroomUser);
        if (existfingerGuess == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 用户参加游戏
     *
     * @param userId
     * @param fingerGuessCacheBean
     * @param existfingerGuess
     * @param chatBody
     */
    private void getWinner(String userId, FingerGuessCacheBean fingerGuessCacheBean, FingerGuessGroupCacheBean existfingerGuess, CmdsResponseBody chatBody) {
        String playerOne = existfingerGuess.getPlayerOne();
        FingerGuessCacheBean playerOneHand = existfingerGuess.getPlayerOneHand();
        int result = existfingerGuess.playerTwoJoinGame(userId, fingerGuessCacheBean);
        chatBody.getMessages().add("== == == == == == == == == == == == == == == == == == == == == == == ==");
        chatBody.getMessages().add("== 游戏播报：玩家：" + playerOne + "发起发起的猜拳游戏，游戏参与者：" + userId);
        chatBody.getMessages().add("== 游戏播报：玩家1：" + playerOne + "（" + playerOneHand.getName() +
                "）  玩家2：" + userId + "（" + fingerGuessCacheBean.getName() + "） ");
        switch (result) {
            case 0: {
                chatBody.getMessages().add("== 游戏播报：游戏结果：平局 ");
                break;
            }
            case 1: {
                chatBody.getMessages().add("== 游戏播报：游戏结果：玩家" + playerOne + playerOneHand.getName() +"胜利 ");
                break;
            }
            case -1: {
                chatBody.getMessages().add("== 游戏播报：游戏结果：玩家" + userId + fingerGuessCacheBean.getName() + "胜利 ");
                break;
            }
            default:
                System.out.println("出现异常");
        }
        chatBody.getMessages().add("== == == == == == == == == == == == == == == == == == == == == == == == == ==");
    }

    /**
     * 猜拳游戏引导
     *
     * @param msg
     */
    private void fingerGuide(List<String> msg) {
        msg.add("== == == == == == == == == == == == == == == == == == == == == == == ==");
        msg.add("== 猜拳游戏： 剪刀(shears) 石头(rock) 布(paper) ");
        msg.add("== 开启一局游戏：例：finger shears (发起一局猜拳游戏，且您本次游戏出拳为剪刀) ");
        msg.add("== 响应一局游戏：例：refinger rock (响应一局猜拳游戏，且您本次游戏出拳为石头) ");
        msg.add("== 响应一局游戏：例：refinger aaa paper (响应用户aaa的猜拳游戏，且您本次游戏出拳为布) ");
        msg.add("== 赶快开启一局新游戏，与房间内的小伙伴一起愉快的玩耍吧！！ ");
        msg.add("== == == == == == == == == == == == == == == == == == == == == == == == == ==");
    }

    /**
     * 游戏发起通知
     *
     * @param userId
     * @param chatBody
     */
    private void fingerNotice(String userId, CmdsResponseBody chatBody) {
        chatBody.getMessages().add("== == == == == == == == == == == == == == == == == == == == == == == ==");
        chatBody.getMessages().add("== 游戏播报：玩家：" + userId + "发起一局猜拳游戏 ");
        chatBody.getMessages().add("== 游戏播报：使用命令refinger来响应游戏或使用来finger了解游戏详情吧 ");
        chatBody.getMessages().add("== == == == == == == == == == == == == == == == == == == == == == == == == ==");
    }

}
