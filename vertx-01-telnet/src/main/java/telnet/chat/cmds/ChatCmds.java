package telnet.chat.cmds;

import telnet.chat.cache.bean.ChatRoomCacheBean;
import telnet.chat.cache.ChatCache;
import telnet.core.common.CmdsResponseBody;
import telnet.finger.model.FingerModel;
import telnet.login.model.LoginModel;
import telnet.util.GuideUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author muyi
 * @description:
 * @date 2020-10-26 16:44:58
 */
public class ChatCmds {

    private LoginModel loginModel;
    private FingerModel fingerModel;
    private ChatCache chatCache;
    /**
     * 日志
     */
    public static final Logger log = Logger.getLogger(ChatCmds.class.getName());

    public ChatCmds() {
        loginModel = LoginModel.getInstance();
        fingerModel = FingerModel.getInstance();
        chatCache = ChatCache.getInstance();
    }

    /**
     * 加入房间
     *
     * @param commands
     * @return
     */
    public List<CmdsResponseBody> join(String userId, String[] commands) {

        ArrayList<CmdsResponseBody> bodys = new ArrayList<>();
        CmdsResponseBody body = new CmdsResponseBody();

        // 1.参数校验
        if ("".equals(userId) || !loginModel.isUserOnline(userId)) {
            body.getMessages().add("您还没有登录，请先登录！");
        } else if (isUserChating(userId)) {
            body.getMessages().add("您已在房间中，如需加入其他房间，请先退出当前聊天室！");
        } else if (commands.length != 2) {
            body.getMessages().add("请输入正确的命令！");
        } else {

            // 2.房间不存在，则创建；房间存在，则用户直接进入房间
            ChatRoomCacheBean chatRoomCacheBean = chatCache.getChatroomByName(commands[1]);
            if (chatRoomCacheBean == null) {
                chatRoomCacheBean = new ChatRoomCacheBean(commands[1]);
                chatCache.addChatroom(chatRoomCacheBean);
            }
            // 2.1将用户添加进房间
            chatRoomCacheBean.getChatUsers().add(userId);
            // 2.2添加用户房间映射
            chatCache.addUserChatroom(userId, chatRoomCacheBean.getChatRoomName());

            // 3.返回用户进入房间数据
            body.getMessages().add("进入入房间：" + commands[1]);
            GuideUtil.userGuide.put(userId, 2);
        }
        body.getUserIds().add(userId);
        GuideUtil.setUserGuide(body.getMessages(), userId);
        bodys.add(body);

        // 4.输出日志
        log.info("用户" + userId + "进房日志：" + bodys.toString());

        return bodys;
    }

    /**
     * 公聊
     *
     * @param commands
     * @return
     */
    public List<CmdsResponseBody> talk(String userId, String[] commands) {

        ArrayList<CmdsResponseBody> bodys = new ArrayList<>();
        CmdsResponseBody body = new CmdsResponseBody();

        // 1.参数校验
        if ("".equals(userId) || !loginModel.isUserOnline(userId)) {
            body.getMessages().add("您还没有登录，请先登录！");
            GuideUtil.setUserGuide(body.getMessages(), userId);
        } else if (!isUserChating(userId)) {
            body.getMessages().add("您还没有加入房间，请先加入房间！");
            GuideUtil.setUserGuide(body.getMessages(), userId);
        } else if (commands.length < 2) {
            body.getMessages().add("请输入聊天内容！");
            GuideUtil.setUserGuide(body.getMessages(), userId);
        } else {

            // 2.获取聊天内容
            String content = getContent(commands, 1);
            // 2.1获取用户所在房间
            ChatRoomCacheBean chatRoomCacheBean = chatCache.getChatRoomByUserId(userId);
            content = "(" + chatRoomCacheBean.getChatRoomName() + ")" + userId + "：" + content;
            // 2.2 获取房间内所有用户
            CmdsResponseBody chatBody = new CmdsResponseBody();
            chatBody.getUserIds().addAll(chatRoomCacheBean.getChatUsers());
            chatBody.getMessages().add(content);

            // 3.设置返回信息
            bodys.add(chatBody);
        }
        body.getUserIds().add(userId);
        bodys.add(body);

        // 4.输出日志
        log.info("用户" + userId + "公聊日志：" + bodys.toString());

        return bodys;
    }

    /**
     * 私聊
     *
     * @param commands
     * @return
     */
    public List<CmdsResponseBody> tell(String userId, String[] commands) {

        ArrayList<CmdsResponseBody> bodys = new ArrayList<>();
        CmdsResponseBody body = new CmdsResponseBody();

        // 1.参数校验
        if ("".equals(userId) || !loginModel.isUserOnline(userId)) {
            body.getMessages().add("您还没有登录，请先登录！");
        } else if (commands.length < 3 || commands[1].indexOf("/") != 0) {
            body.getMessages().add("请输入正确的格式！");
        } else if (!loginModel.isUserOnline(commands[1].substring(1))) {
            body.getMessages().add("抱歉！您私聊的用户不在线上！");
        } else {

            // 2.获取聊天内容
            String content = getContent(commands, 2);
            content = "(私聊)" + userId + "：" + content;

            // 3.设置返回信息
            body.getMessages().add("私聊信息发送成功，您可以继续发送私聊！");
            CmdsResponseBody chatBody = new CmdsResponseBody();
            chatBody.getUserIds().add(commands[1].substring(1));
            chatBody.getMessages().add(content);
            bodys.add(chatBody);
        }
        body.getUserIds().add(userId);
        GuideUtil.setUserGuide(body.getMessages(), userId);
        bodys.add(body);

        // 4.输出日志
        log.info("用户" + userId + "私聊日志：" + bodys.toString());

        return bodys;
    }

    /**
     * 退出房间
     *
     * @param commands
     * @return
     */
    public List<CmdsResponseBody> quit(String userId, String[] commands) {

        ArrayList<CmdsResponseBody> bodys = new ArrayList<>();
        CmdsResponseBody body = new CmdsResponseBody();

        // 1.参数校验
        if ("".equals(userId) || !loginModel.isUserOnline(userId)) {
            body.getMessages().add("您还没有登录，请先登录！");
        } else if (!isUserChating(userId)) {
            body.getMessages().add("您没有加入任何房间，请先加入房间！");
        } else if (commands.length != 1) {
            body.getMessages().add("==请输入正确的命令！==");
        } else {

            // 2.用户退出房间
            removeUserChat(userId);

            // 3.设置返回数据
            body.getMessages().add("退出房间,回到主界面");
            GuideUtil.userGuide.put(userId, 1);
        }
        body.getUserIds().add(userId);
        GuideUtil.setUserGuide(body.getMessages(), userId);
        bodys.add(body);

        // 4.输出日志
        log.info("用户" + userId + "私聊日志：" + bodys.toString());

        return bodys;

    }

    /**
     * 获取包括index坐标之后的所有内容
     *
     * @param commands
     * @param index
     * @return
     */
    private String getContent(String[] commands, int index) {
        String content = "";
        for (int i = index; i < commands.length; i++) {
            content += commands[i] + " ";
        }
        return content;
    }

    /**
     * 删除房间中的用户，如果房间人数为0，房间销毁
     *
     * @param userId
     */
    public void removeUserChat(String userId) {
        if (isUserChating(userId)) {
            ChatRoomCacheBean chatroom = chatCache.getChatRoomByUserId(userId);
            List<String> chatUsers = chatroom.getChatUsers();
            chatUsers.remove(userId);
            chatCache.delUserChatroomByUserId(userId);

            if (!(chatUsers.size() == 0)) {
                log.info("房间：" + chatroom + " 的人数为" + chatUsers.size());
            } else {
                chatCache.delChatroomByName(chatroom.getChatRoomName());
                fingerModel.delFingerGuessByChatroomName(chatroom.getChatRoomName());
                log.info("房间：" + chatroom + " 的人数为0， 房间销毁，且销毁房间中的游戏");
            }
        }

    }

    /**
     * 判断用户是否在房间中
     *
     * @param userId
     * @return
     */
    public boolean isUserChating(String userId) {
        String chatRoomName = chatCache.getChatroomNameByUserId(userId);
        if (chatRoomName == null || "".equals(chatRoomName)) {
            return false;
        } else {
            return true;
        }
    }

}
