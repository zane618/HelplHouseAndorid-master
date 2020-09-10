package com.zidiv.realty.urls;

/**
 * 接口请求地址
 * Created by Administrator on 2016/3/11.
 */
public class HttpUrls {

//    public static final String SERVER_URL = "http://mobile.ahgbjy.gov.cn/";

    //public static final String SERVER_URL = "http://www.80mf.com:9080/";
    public static final String SERVER_URL = "http://dh.80mf.com/";
    //1.登录
    public static final String LOGIN_URL = SERVER_URL + "Account/Login";

    //获取资料
    public static final String GETUPDATE_URL = SERVER_URL + "Account/Update?type=android";
    //2.注册
        //获取验证码
    public static final String GETKEY_URL = SERVER_URL + "Account/GetKey?phone=";
        //验证码验证
    public static final String ISKEY_URL = SERVER_URL + "Account/IsKey?phone=";
        //注册信息保存
    public static final String REGISTER_URL = SERVER_URL + "Account/Register";
    //获取banner图
    public static final String BANNER_URL = SERVER_URL + "Account/Banner";

    //3.忘记密码
        //获取验证码
    public static final String GETBACKKEY_URL = SERVER_URL + "Account/GetBackKey?phone=";
        //验证码验证
    public static final String ISKEY_URL2 = SERVER_URL + "Account/IsKey?phone=";
        //密码保存
    public static final String GETBACKPWD_URL = SERVER_URL + "Account/GetBackPWD";
    //4.修改密码
    public static final String CHANGEPWD_URL = SERVER_URL + "User/ChangePWD";
    //5.注销
    public static final String LOGINOUT_URL = SERVER_URL + "User/LoginOut";

    //我的资料
        //获取资料
    public static final String GETINFO_URL = SERVER_URL + "User/Info";
        //资料更新
    public static final String UPDATEINFO_URL = SERVER_URL + "User/UpdateInfo";

    //修改头像
    public static final String AVATAR_URL = SERVER_URL + "User/Avatar";

    //修改头像
    public static final String CARDFRONT_URL = SERVER_URL + "User/IDCardFront";
    //修改头像
    public static final String CARDBACK_URL = SERVER_URL + "User/IDCardBack";

    //推送设置
    public static final String PUSHINFO_URL = SERVER_URL + "JPush/Info";
    //推送设置
    public static final String PUSHADD_URL = SERVER_URL + "JPush/Add";
    //推送设置
    public static final String PUSHUPDATE_URL = SERVER_URL + "JPush/Update";

    //我的收藏
        //收藏列表
    public static final String MYCOLLECT_URL = SERVER_URL + "Business/ActivityList";
        //添加收藏
    public static final String ADDCOLLECT_URL = SERVER_URL + "Store/Add";
        //取消收藏
    public static final String DELETECOLLECT_URL = SERVER_URL + "Store/Delete";

    //联系信息
        //联系信息列表
    public static final String CONTACTLIST_URL = SERVER_URL + "Contact/List";
        //添加联系信息
    public static final String ADDCONTACTLIST_URL = SERVER_URL + "Contact/Add";
        //修改联系信息
    public static final String MODIFYCONTACTLIST_URL = SERVER_URL + "Contact/Update";
        //删除联系信息
    public static final String DELETECONTACTLIST_URL = SERVER_URL + "Contact/Delete";

    //获取价格
    public static final String PUSHCHANGE_URL = SERVER_URL + "Push/Update";

    //获取价格
    public static final String PRICE_URL = SERVER_URL + "Order/Price";

    //支付宝支付
    public static final String ALIPAY_URL = SERVER_URL + "Order/AliPayOrderV4";
    //微信支付
    public static final String WECHATPAY_URL = SERVER_URL + "Order/WeChatOrderV4";


    //广告设置
        //1.导航栏接口   1是轮播图   2貌似是关于我们
    public static final String ADVERTISING_URL = "http://api.zidiv.com:9005/Account/Banner?position_id=";

    //出售
    public static final String HOUSE_SNAME_LIST = SERVER_URL + "House/SearchName";
    //出售
    public static final String HOUSE_SALE_LIST = SERVER_URL + "House/Sale";
    //出租
    public static final String HOUSE_RENT_LIST = SERVER_URL + "House/Rent";
    //附近
    public static final String HOUSE_NEAR_LIST = SERVER_URL + "House/Near";
    //我的浏览
    public static final String HOUSE_BROWSER_LIST = SERVER_URL + "Browers/List";
    //浏览
    public static final String HOUSE_BROWSER_ADD = SERVER_URL + "Browers/Add";
    //我的跟进
    public static final String HOUSE_GO_LIST = SERVER_URL + "GoTo/List";
    //搜索
    public static final String HOUSE_LIST = SERVER_URL + "House/Index";
    //搜索
    public static final String HOUSECOLLECT_LIST = SERVER_URL + "House/CollectIndex";

    //跟进列表
    public static final String HOUSE_GENJIN_LIST = SERVER_URL + "GoTo/InfoList";
    //跟进
    public static final String HOUSE_GOTO = SERVER_URL + "GoTo/Add";
    //收藏
    public static final String HOUSE_COLLECT = SERVER_URL + "Collect/Add";
    //取消收藏
    public static final String HOUSE_UNCOLLECT = SERVER_URL + "Collect/Delete";
    //收藏列表
    public static final String HOUSE_COLLECT_LIST = SERVER_URL + "Collect/List";
    //是否收藏
    public static final String HOUSE_ISCOLLECT = SERVER_URL + "Collect/Exists";

}
