package com.xkhouse.fang.app.config;


/**
 * 存放常量
 * @author wujian
 *
 */
public class Constants {

	// androidID
	public static String ANDROID_ID;


	public static String HOST_OLD = "http://xkapi.com";
	public static String HOST = "https://api.tiqianding.com";


	/******************************* 全局通用  *******************************/

    /**  版本更新  **/
    public static String APP_VERSION_CHECK = HOST_OLD + "/v1.0/System/ApplicationVersion.api";

	/**  专题资讯  **/
	public static String SITE_LIST = HOST_OLD + "/v1.0/System/Site.api";
	
	/**  通过配置标识获取相应配置子选项列表   **/
	public static String CONFIG_LIST = HOST_OLD + "/v1.0/System/Config.api";
	
	/**  短信验证码接口    **/
	public static String GET_VERIFY_CODE = HOST_OLD + "/v1.0/Other/Sms.api";
	
	/**  根据站点ID获取该站点热搜关键词     **/
	public static String SEARCH_HOT_WORDS = HOST_OLD + "/v1.0/Other/SearchHot.api";
	
	/**  根据关键词匹配新房楼盘名、二手房小区名、租房小区名返回---只匹配楼盘名、小区名     **/
	public static String SEARCH_PROJECT_NAME = HOST_OLD + "/v1.0/Other/SearchProjectName.api";

    /**  根据关键词匹配学区     **/
    public static String SEARCH_SCHOOL_NAME = HOST_OLD + "/v1.0/Newhouse/SearchSchool.api";
	
	/**  根据关键词匹配新房、二手房、租房、资讯，返回相应数据数量---全文匹配     **/
	public static String SEARCH_KEY_WORD = HOST_OLD + "/v1.0/Other/SearchKeyword.api";
	
	/**  星空贷 提交信息接口     **/
	public static String XK_LOAN_COMMIT = HOST_OLD + "/v1.0/Other/Loan.api";
	
	/**  上传图片     **/
	public static String UPLOAD_PHOTO_URL = "http://upload.xkhouse.com/server/controller.php?action=uploadimage&encode=utf-8";
	
	
	
	
	/******************************* 首页相关  *******************************/

    /** 广告   **/
    public static String TQD_AD = HOST + "/v1/Index/advertisement";

	public static String SPLASH_AD_LIST = HOST_OLD + "/v1.0/Other/Ad.api";
			
	/** 首页轮询图 **/
	public static String INDEX_BANNER_LIST = HOST+ "/v1/Index/carousel";
	
	/** 新房推荐(猜你喜欢)   **/
	public static String HOUSE_LIKE = HOST_OLD + "/v1.1/Newhouse/Guess.api";
	
	/** 资讯推荐(猜你喜欢)   **/
	public static String NEWS_LIKE = HOST_OLD + "/v1.2/News/Guess.api";




    /** 预定列表   **/
    public static String BOOKEDINFO_LIST = HOST + "/v1/booking/booking_list";

    /** 首页获取抽奖信息   **/
    public static String LUCKINFO_LIST = HOST + "/v1.index/getIndexLottery";


    /** 抽奖活动列表   **/
    public static String CJ_LIST = HOST + "/v1.index/getListLottery";



    /**  专题资讯  **/
	public static String NAVIGATION_LIST = HOST_OLD + "/v1.1/System/Navigation.api";
	
	/**  热门活动  **/
	public static String HOT_ACTIVITY_LIST = HOST_OLD + "/v1.0/newhouse/activity.api";
	
	/**  家居报名  **/
	public static String JJ_COMMIT = HOST_OLD + "/v1.0/Other/Yuyue.api";

    /** 获取看房团信息  **/
    public static String KANFANG = HOST_OLD + "/v1.0/Newhouse/Kanfang.api";

    /** 获取房市行情信息  **/
    public static String FSHQ = HOST_OLD + "/v1.0/Newhouse/Fshq.api";



    /******************************* 预定 *******************************/

    /** 商家详情  **/
    public static String STORE_DETAIL = HOST + "/v1/Business/business_detail";

    /** 商家评论  **/
    public static String STORE_COMMENT = HOST + "/v1/business/business_comment";

    /** 商户相册分类  **/
    public static String STORE_ALBUM_CATEGORY = HOST + "/v1/business/business_album_category";

    /** 商户相册  **/
    public static String STORE_ALBUM = HOST + "/v1/business/business_album";

    /** 获取收货地址  **/
    public static String ADDRESS_LIST = HOST + "/v1/My/my_address_list";

    /** 修改收货地址  **/
    public static String ADDRESS_EDIT = HOST + "/v1/My/my_address_edit";

    /** 添加收货地址  **/
    public static String ADDRESS_ADD = HOST + "/v1/My/my_address_add";

    /** 删除收货地址  **/
    public static String ADDRESS_DELETE = HOST + "/v1/My/my_address_del";


	
	/******************************* 房源相关  *******************************/
	
	/**  某站点的行政区域列表  **/
	public static String AREA_LIST = HOST_OLD + "/v1.0/Newhouse/Area.api";
	
	/**  某站点的行政区域列表 返回了楼盘个数 地图找房 **/
	public static String MAP_AREA_LIST = HOST_OLD + "/v1.1/Newhouse/Area.api";
	
	/**  某站点的重点学校  **/
	public static String SCHOOL_LIST = HOST_OLD + "/v1.0/Newhouse/School.api";
	
	/**  网友定制列表  **/
	public static String CUSTOM_HOUSE_LIST = HOST_OLD + "/v1.1/Newhouse/Dingzhi.api";
	
	/**  定制购房需求发布  **/
	public static String CUSTOM_HOUSE_ADD = HOST_OLD + "/v1.1/Newhouse/DingzhiPost.api";
	
	/**  新房列表  **/
	public static String NEW_HOUSE_LIST = HOST_OLD + "/v1.2/Newhouse/List.api";

    /**  新房列表  **/
    public static String NEW_HOUSE_TYPE_LIST = HOST_OLD + "/v1.0/Newhouse/Housetypelist.api";

	/**  获取列表条件切换（二手房，租房）  **/
	public static String OLD_HOUSE_TAB_LIST = HOST_OLD + "/v1.0/Oldhouse/getBaseTab.api";
	
	/**  二手房出售列表  **/
	public static String OLD_HOUSE_LIST = HOST_OLD + "/v1.0/Oldhouse/List.api";
	
	/**  租房出售列表  **/
	public static String RENT_HOUSE_LIST = HOST_OLD + "/v1.0/Zufang/List.api";
	
	/**  获取各区域内出售房源总数     二手房地图找房  **/
	public static String OLD_HOUSE_COUNT = HOST_OLD + "/v1.0/Oldhouse/GetCount.api";
	
	/**  根据小区名称等参数条件获取小区出售房源信息列表     二手房地图找房  **/
	public static String MAP_OLD_HOUSE_LIST = HOST_OLD + "/v1.0/Oldhouse/GetSaleHouse.api";
	
	/**  获取各区域内出售房源总数     二手房地图找房  **/
	public static String OLD_HOUSE_COMMUNITY_COUNT = HOST_OLD + "/v1.0/Oldhouse/GetMap.api";
	
	/**  获取各区域内出租房源总数     租房地图找房  **/
	public static String RENT_HOUSE_COUNT = HOST_OLD + "/v1.0/Zufang/GetCount.api";
	
	/**  楼盘主力户型图   **/
	public static String HOUSE_TYPE_LIST = HOST_OLD + "/v1.0/Newhouse/Housetype.api";
	
	/**  在售房源   **/
	public static String MAIN_HOUSE_LIST = HOST_OLD + "/v1.0/Newhouse/Choice.api";
	
	/**  楼盘销售动态   **/
	public static String HOUSE_DYNAMIC_LIST = HOST_OLD + "/v1.0/Newhouse/Dynamic.api";

    /**  购房能力评估   **/
    public static String HOUSE_PING_GU = HOST_OLD + "/v1.0/Newhouse/Pinggu.api";

    /**  预约看房报名  **/
    public static String HOUSE_KAN_FANG = HOST_OLD + "/v1.0/Newhouse/Yuyuekanfang.api";

    /**  学区房列表  **/
    public static String HOUSE_SCHOOL_LIST = HOST_OLD + "/v1.1/Newhouse/SchoolDistrict.api";

    /**  楼盘问答列表  **/
    public static String HOUSE_QUESTION_LIST = HOST_OLD + "/v1.0/Newhouse/QuestionList.api";

    /**  楼盘提问发布  **/
    public static String HOUSE_QUESTION_ADD = HOST_OLD + "/v1.0/Newhouse/QuestionAdd.api";

    /**  学校划片小区  **/
    public static String SCHOOL_COMMUNITY_LIST = HOST_OLD + "/v1.0/Oldhouse/Xuequ.api";


	/******************************* 星空宝相关  *******************************/
	/**  星空宝楼盘列表   **/
	public static String XKB_HOUSE_LIST = HOST_OLD + "/v1.0/Group/Commission.api";
	
	/**  星空宝推荐客户  **/
	public static String XKB_CUSTOMER_ADD = HOST_OLD + "/v1.0/Group/Recommend.api";
	
	/**  星空宝排序  **/
	public static String XKB_ORDER_LIST = HOST_OLD + "/v1.0/Group/CommissionOrder.api";
	
	/**  星空宝期望楼盘  **/
	public static String XKB_EXPECT_HOUSE_LIST = HOST_OLD + "/v1.0/Group/GroupDataList.api";
	
	/**  星空宝提现  **/
	public static String XKB_CASH_WITHDRAW = HOST_OLD + "/v1.0/Group/Withdrawals.api";
	
	/**  星空宝推荐列表 **/
	public static String XKB_RECOMMEND_LIST = HOST_OLD + "/v1.1/Group/MyRecom.api";
	
	/**  星空宝推荐客户详情 **/
	public static String XKB_RECOMMEND_DETAIL = HOST_OLD + "/v1.0/Group/RecomInfo.api";
	
	/**  星空宝我的钱包--账单 **/
	public static String XKB_WALLET_LIST = HOST_OLD + "/v1.1/Group/MyWallet.api";
	
	/**  星空宝我的钱包--银行列表 **/
	public static String XKB_BANK_LIST = HOST_OLD + "/v1.0/Group/Bank.api";

    /**  星空宝我的钱包--最近提现记录 **/
    public static String XKB_TX_RECORD__LIST = HOST_OLD + "/v1.0/Group/Flowingwater.api";

    /**  星空宝我的推荐--再次推荐 **/
    public static String XKB_RECOMMEND_AGIAN = HOST_OLD + "/v1.0/Group/SetMyRecom.api";


	
	
	
	
	/******************************* 个人中心相关  *******************************/

    /**  我的预定列表  **/
    public static String USER_BOOKED_LIST = HOST + "/v1/Interactive/booking_list";

    /**  我的预定详情  **/
    public static String USER_BOOKED_DETAIL = HOST + "/v1/Interactive/booking_detail";

    /**  在线预定  **/
    public static String USER_BOOK_ADD = HOST + "/v1/Interactive/booking_add";

    /**  我的评价列表  **/
    public static String USER_COMMENT_LIST = HOST + "/v1/My/my_commemt_list";

    /**  我的评价列表  **/
    public static String USER_COMMENT_ADD = HOST + "/v1/My/my_commemt_add";

    /**  添加收藏  **/
    public static String USER_FAV_ADD = HOST + "/v1/My/my_collection_add";

    /**  添加足迹  **/
    public static String USER_FOOT_ADD = HOST + "/v1/My/my_footprint_add";


	/**  我的消息  **/
	public static String USER_NEWS = HOST_OLD + "/v1.0/Passport/News.api";
	
	/**  我的消息二级列表  **/
	public static String USER_MESSAGE_LIST = HOST_OLD + "/v1.0/Passport/Message.api";
	
	/**  注册  **/
	public static String USER_REGISTER = HOST_OLD + "/v1.1/Passport/UserRegister.api";
	
	/**  登录  **/
    public static String USER_LOGIN = HOST + "/v1/login/account_login";

    /**  手机号快捷登录  **/
    public static String USER_LOGIN_PHONE = HOST + "/v1/login/verify_login";

    /**  设置密码  **/
    public static String USER_PSW_SET = HOST + "/v1/login/set_password";
	
	/**  QQ登录  **/
	public static String USER_QQ_LOGIN = HOST_OLD + "/v1.1/Passport/QqLogin.api";

    /**  微信登录  **/
    public static String USER_WX_LOGIN = HOST_OLD + "/v1.1/Passport/WeChatLogin.api";
	
	/**  新浪微博登录  **/
	public static String USER_WEIBO_LOGIN = HOST_OLD + "/v1.1/Passport/WeiboLogin.api";
	
	/**  找回密码第一步  **/
	public static String USER_MOBILE_CODE = HOST_OLD + "/v1.0/Passport/GetMobileCode.api";
	
	/**  重置密码 **/
	public static String USER_RESET_PSW = HOST + "/v1/My/my_edit_password";
	
	/**  修改密码 **/
	public static String USER_CHANGE_PSW = HOST_OLD + "/v1.0/Passport/ChangePassWord.api";

    /**  设置密码 **/
    public static String USER_SET_PSW = HOST_OLD + "/v1.1/Passport/ChangePassWord.api";

	/**  反馈意见  **/
	public static String USER_FEED_BACK = HOST_OLD + "/v1.0/Passport/FeedBack.api";
	
	/**  用户信息  **/
	public static String USER_INFO = HOST + "/v1/My/my_index";
	
	/**  支付密码设置/修改  **/
	public static String USER_CHANGE_PAY_PSW = HOST_OLD + "/v1.0/Passport/PayPassWord.api";
	
	/**  修改个人信息  **/
	public static String USER_INFO_EDIT = HOST_OLD + "/v1.0/Passport/ModInfo.api";
	
	/**  手机号修改接口(老用户登录后手机号没设置的)   **/
	public static String USER_CHANGE_MOBILE = HOST_OLD + "/v1.0/Passport/ChangeMobile.api";
	
	/**  消息，收藏条数   **/
	public static String USER_NEWS_FAVORITE_COUNT = HOST_OLD + "/v1.0/Passport/Newsnum.api";
	
	/**  收藏  **/
	public static String USER_FAVORITE_ADD = HOST_OLD + "/v1.0/Passport/Favorite.api";
	
	/**  是否收藏  **/
	public static String USER_FAVORITE_STATUS = HOST_OLD + "/v1.0/Passport/FavoriteNy.api";
	
	/**  收藏列表  **/
	public static String USER_FAVORITE_LIST = HOST_OLD + "/v1.0/Passport/Favoritelist.api";
	
	/**  刪除,清空收藏  **/
	public static String USER_FAVORITE_EDIT = HOST_OLD + "/v1.0/Passport/FavoriteEdit.api";

    /**  修改手机号  **/
    public static String USER_MOBILE_RESET = HOST + "/v1/My/my_edit_phone";

    /**  修改昵称  **/
    public static String USER_NICKNAME_CHANGE = HOST + "/v1/My/my_edit_nickname";




    /**  修改手机号  **/
    public static String USER_PAYPSW_FIND = HOST_OLD + "/v1.0/Passport/FindPayPassWord.api";

    /**  后台判断第三方是否绑定  **/
    public static String USER_BIND_STATUS = HOST_OLD + "/v1.0/Passport/ThirdBind.api";

    /**  后台QQ账号绑定/解绑  **/
    public static String USER_QQ_BIND = HOST_OLD + "/v1.0/Passport/QqBind.api";

    /**  后台新浪微博账户绑定/解绑  **/
    public static String USER_SINA_BIND = HOST_OLD + "/v1.0/Passport/WeiboBind.api";

    /**  后台微信账户绑定/解绑  **/
    public static String USER_WX_BIND = HOST_OLD + "/v1.0/Passport/WeChatBind.api";

    /**  前台第三方qq解绑  **/
    public static String USER_QQ_REBIND = HOST_OLD + "/v1.0/Passport/QqUnBind.api";

    /**  前台第三方微信解绑  **/
    public static String USER_WX_REBIND = HOST_OLD + "/v1.0/Passport/WeChatUnBind.api";

    /**  前台第三方qq解绑  **/
    public static String USER_SINA_REBIND = HOST_OLD + "/v1.0/Passport/WeiboUnBind.api";

    /**  获取有无新消息  **/
    public static String USER_MESSAGE_READ = HOST_OLD + "/v1.0/Passport/MessageRead.api";

    /**  获取邀请码接口  **/
    public static String USER_INVITATION_CODE = HOST_OLD + "/v1.0/Passport/GetInviteCode.api";



    /**  发布求购房源  **/
    public static String USER_SELL_IN = HOST_OLD + "/v1.0/Esf/AddBuy.api";

    /**  编辑求购房源  **/
    public static String USER_SELL_IN_EDIT = HOST_OLD + "/v1.0/Esf/EditBuy.api";

    /**  获取求购房源信息  **/
    public static String USER_SELL_IN_INFO = HOST_OLD + "/v1.0/Esf/GetBuyInfo.api";

    /**  发布求租房源  **/
    public static String USER_RENT_IN = HOST_OLD + "/v1.0/Esf/AddRent.api";

    /**  编辑求租房源  **/
    public static String USER_RENT_IN_EDIT = HOST_OLD + "/v1.0/Esf/EditRent.api";

    /**  发布求租房源  **/
    public static String RELEASE_AREA = HOST_OLD + "/v1.0/Esf/GetAreaTree.api";

    /**  获取求租房源信息  **/
    public static String USER_RENT_IN_INFO = HOST_OLD + "/v1.0/Esf/GetRentInfo.api";

    /**  发布出售房源  **/
    public static String USER_SELL_RELEASE = HOST_OLD + "/v1.0/Esf/AddSale.api";

    /**  获取出售房源信息 **/
    public static String USER_SELL_RELEASE_INFO = HOST_OLD + "/v1.0/Esf/GetSaleInfo.api";

    /**  编辑出售房源信息 **/
    public static String USER_SELL_RELEASE_EDIT = HOST_OLD + "/v1.0/Esf/EditSale.api";


    /**  发布出租房源  **/
    public static String USER_RENT_RELEASE = HOST_OLD + "/v1.0/Esf/AddHire.api";

    /**  发布出租房源  **/
    public static String USER_RENT_RELEASE_EDIT = HOST_OLD + "/v1.0/Esf/EditHire.api";

    /**  获取出租房源  **/
    public static String USER_RENT_RELEASE_INFO = HOST_OLD + "/v1.0/Esf/GetHireInfo.api";



    /**  发布房源--选择小区  **/
    public static String USER_AREA_RELEASE = HOST_OLD + "/v1.0/Esf/GetProjectName.api";

    /**  获取出租房源列表  **/
    public static String USER_RENT_RELEASE_LIST = HOST_OLD + "/v1.0/Esf/HireList.api";

    /**  获取求租房源列表  **/
    public static String USER_RENT_IN_LIST = HOST_OLD + "/v1.0/Esf/RentList.api";

    /**  获取出售房源列表  **/
    public static String USER_SELL_RELEASE_LIST = HOST_OLD + "/v1.0/Esf/SaleList.api";

    /**  获取求购房源列表  **/
    public static String USER_SELL_IN_LIST = HOST_OLD + "/v1.0/Esf/BuyList.api";

    /**  个人和经纪人删除房源(软删)  **/
    public static String USER_RELEASE_DELETE = HOST_OLD + "/v1.0/Esf/DeleteHouseInfo.api";

    /**  个人和经纪人刷新房源  **/
    public static String USER_RELEASE_REFRESH = HOST_OLD + "/v1.0/Esf/Refresh.api";

    /**  重新发布房源   **/
    public static String USER_RELEASE_AGAIN = HOST_OLD + "/v1.0/Esf/ReIssue.api";

    /**  经纪人彻底删除房源   **/
    public static String USER_RELEASE_CLEAR = HOST_OLD + "/v1.0/Esf/MiddlemanDeleteThrough.api";

    /**  上架下架房源    **/
    public static String USER_RELEASE_SHANG_XIA_JIA = HOST_OLD + "/v1.0/Esf/ShelveOrUnshelve.api";





	
	//网络获取的数据为空
    public static final int NO_DATA_FROM_NET = -1;
    
    //请求网络异常
    public static final int ERROR_DATA_FROM_NET = -2;
    
    //成功获取网络数据
    public static final int SUCCESS_DATA_FROM_NET = 0;
    
    
    //返回数据成功code至
    public static final String SUCCESS_CODE_OLD = "101";
    public static final String SUCCESS_CODE = "1";

    
    /**  qq 第三方登录  **/
    public static final String LOGIN_TYPE_QQ = "QQ_LOGIN";

    /**  新浪微博 第三方登录  **/
    public static final String LOGIN_TYPE_WX = "WX_LOGIN";

    /**  新浪微博 第三方登录  **/
    public static final String LOGIN_TYPE_WEIBO = "SINA_LOGIN";
    
    
    /** Volley  配置 **/
    public static int VOLLEY_TIME_OUT = 10*1000;       //超时时间
    public static int VOLLEY_MAX_NUM_RETRIES = 1;      //重试次数
    public static float VOLLEY_BACKOFF_MULTIPLIER = 1.0f;


    public static final String USER_GE_REN = "1";
    public static final String USER_JING_JI_REN = "2";


}
