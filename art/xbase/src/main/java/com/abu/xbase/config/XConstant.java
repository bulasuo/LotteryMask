package com.abu.xbase.config;

/**
 * @author abu
 *         2017/11/14    09:54
 *         bulasuo@foxmail.com
 */

public class XConstant {

    public interface ShareP{
        /**
         * 是否登录
         */
        String LOGIN = "sp_login";

        /**
         * 帐号
         */
        String ACCT_NO = "sp_account";

        /**
         * 账户名
         */
        String ACCT_NAME = "sp_acct_name";
        String PHONE = "sp_phone";


        /**
         * 头像
         */
        String HEAD_IMG = "sp_head_img";

        /**
         * 性别
         */
        String GENDER = "sp_gender";

        /**
         * 地址
         */
        String OCCUPATION = "sp_occupation";

        /**
         * 写字楼ID
         */
        String CBD_ADDR_ID = "sp_cbd_addr_id";

        String CBD_ADDR_ID_remark = "sp_cbd_addr_id_remark";

        /**
         * 楼层
         */
        String FLOOR_NM = "sp_floor_nm";

        /**用户定位的城市*/
        String CITY_BEAN =  "sp_city_bean";

        /**
         * 登录权限token
         */
        String TOKEN = "sp_token";

        String OCCUPATION_LIST = "sp_occupation_list";
        String TOP_MSG_SET = "sp_top_msg_set";
        String UN_TOP_MSG_SET = "sp_un_top_msg_set";
        String READED_MSGS = "sp_readed_msg_list";
        String SELF_SEND_MSGS = "sp_self_send_msg_list";
//        String SP_PUSH_TOKEN = "SP_PUSH_TOKEN";
    }

    public interface EventBus{
        /**
         * 用来传递startForResult
         */
        int REQUEST_TO_RESULT = 1;

        /**
         * 登录成功 检查账户切换 初始化数据等等
         * 不用作更新信息  int UPDATE_USER_INFO_SUCCESS = 4;//用作更新用户信息
         */
        int LOGIN_SUCCESS = 2;

        /**
         * 登出
         */
        int LOGIN_OUT = 3;

        /**
         * 用作更新用户信息
         */
        int UPDATE_USER_INFO_SUCCESS = 4;

        int CLOSE_PUBLISH_VIEW = 5;

        int NET_CONNECT_SUCCESS = 6;

        /**时间晶振 1秒*/
        int TIME_CRYSTAL_OSCILLATOR = 7;

        int ON_TOP_MESSAGE_CHANGE = 8;

        int ON_MESSAGE_READED = 9;

        int ON_MESSAGE_DETAIL_READED = 10;

        int ON_MESSAGE_CLOSED = 11;

        int REGIST_SUCCESS = 12;

        int RESET_PWD_SUCCESS = 13;

        int UPDATE_APP_CONFIG_VIEW = 14;

        int COURSE_VIEW_SCROLL_TOP = 15;

        int LOAD_CURSOR = 16;

        int VIDEOS_VIEW_SCROLL_TOP = 17;

    }

    public interface RequestCode{
        /**
         * 进入用户信息页面
         */
        int PAGE_USER_INFO = 1;

        /**
         * 发起相册请求
         */
        int REQUEST_ACTION_PICK = 2;

        int REQUEST_CAMERA_CROP = 3;

        /**
         * 来自UserInfoFragment的请求
         */
        int FRAGMENT_USER_INFO_FOR_PICK = 4;

        /**
         * 楼内信息的信息点击
         */
        int PAGE_FLOOR_INFO_MSG_CLICK = 5;

        int PAGE_USER_PUBLISH_MSGS_MSG_CLICK = 6;

        int PAGE_USER_PUBLISH_INFO = 7;

        int PUBLISH_MSG_DETAIL_MSG_CLICK = 8;

        int AUCTION_UPDATE_HEAD_IMG = 9;
        int AUCTION_UPDATE_NICK_NAME = 10;
        int AUCTION_UPDATE_BALANCE = 11;

        int REQUEST_ACTION_CAMERA = 12;

        int REQUEST_ACTION_ENTER_CURSOR = 13;

        int REQUEST_ACTION_ENTER_VIDEO = 14;

        int PAGE_VIDEO_DETAIL_REQUEST_ACTION_RECHARGE = 15;

        int PAGE_COURSE_DETAIL_REQUEST_ACTION_RECHARGE = 16;
    }

    public interface ResultCode{
        /**
         * 登录成功
         */
        int LOGIN_SUCCESS = 1;

        int EDIT_USER_INFO_COMFIRM = 2;

        int REGIST_SUCCESS = 3;

    }
}
