package com.szltech.networklibrary.main;

import android.support.annotation.NonNull;

import com.szltech.networklibrary.utils.ObjectUtils;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle.components.support.RxAppCompatDialogFragment;
import com.trello.rxlifecycle.components.support.RxDialogFragment;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.trello.rxlifecycle.components.support.RxFragmentActivity;

/**
 * DESC:一个http请求接口入口类，一个工厂类，提供通用网络请求接口和合并网络请求功能
 *
 * Created by Jinphy on 2017/11/22.
 */

public class HttpUtils {

    // 工具类
    private HttpUtils(){}

    //=============创建 common API实例=================================

    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/4, at 8:55
     */
    public static ApiInterface<String> common(RxFragmentActivity activity) {
        return new CommonApi<>(activity, String.class);
    }

    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/11, at 9:19
     */
    public static ApiInterface<String> common(RxAppCompatActivity activity) {
        return new CommonApi<>(activity, String.class);
    }

    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/4, at 8:55
     */
    public static ApiInterface<String> common(RxFragment fragment) {
        return new CommonApi<>(fragment, String.class);
    }

    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/4, at 8:55
     */
    public static ApiInterface<String> common(RxDialogFragment fragment) {
        return new CommonApi<>(fragment, String.class);
    }

    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/4, at 8:55
     */
    public static ApiInterface<String> common(RxAppCompatDialogFragment fragment) {
        return new CommonApi<>(fragment, String.class);
    }

    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/4, at 8:55
     */
    public static <U> ApiInterface<U> common(RxFragmentActivity activity, @NonNull Class<U> resultClass) {
        ObjectUtils.requireNonNull(resultClass, "resultClass cannot be null!");
        return new CommonApi<>(activity, resultClass);
    }


    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/4, at 8:55
     */
    public static <U> ApiInterface<U> common(RxAppCompatActivity activity, @NonNull Class<U> resultClass) {
        ObjectUtils.requireNonNull(resultClass, "resultClass cannot be null!");
        return new CommonApi<>(activity, resultClass);
    }


    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/4, at 8:55
     */
    public static <U> ApiInterface<U> common(RxFragment fragment, @NonNull Class<U> resultClass) {
        ObjectUtils.requireNonNull(resultClass, "resultClass cannot be null!");
        return new CommonApi<>(fragment, resultClass);
    }

    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/4, at 8:55
     */
    public static <U> ApiInterface<U> common(RxDialogFragment fragment, @NonNull Class<U> resultClass) {
        ObjectUtils.requireNonNull(resultClass, "resultClass cannot be null!");
        return new CommonApi<>(fragment, resultClass);
    }

    /**
     * DESC: 创建一个网络请求api
     * Created by Jinphy, on 2017/12/4, at 8:55
     */
    public static <U> ApiInterface<U> common(RxAppCompatDialogFragment fragment, @NonNull Class<U> resultClass) {
        ObjectUtils.requireNonNull(resultClass, "resultClass cannot be null!");
        return new CommonApi<>(fragment, resultClass);
    }


    //==========创建 common API实例 ==========================
    public static ApiInterface<Object[]> zipper(RxAppCompatActivity activity){
        return new ZipApi<>(activity,Object[].class);
    }

    public static ApiInterface<Object[]> zipper(RxFragmentActivity activity){
        return new ZipApi<>(activity,Object[].class);
    }
    public static ApiInterface<Object[]> zipper(RxFragment fragment){
        return new ZipApi<>(fragment, Object[].class);
    }
    public static ApiInterface<Object[]> zipper(RxDialogFragment fragment){
        return new ZipApi<>(fragment,Object[].class);
    }
    public static ApiInterface<Object[]> zipper(RxAppCompatDialogFragment fragment){
        return new ZipApi<>(fragment, Object[].class);
    }

    //==========================================================================================\\
    //==============常量=======================================================================//

    /**
     * DESC: 网络请求参数的键常量
     * Created by Jinphy, on 2017/12/4, at 8:54
     */
    public interface Key {

        //=============== params key ===============================================
        String STAT_TYPE = "stattype";
        String MESSAGE_ID = "messageid";
        String ACCESS_TOKEN = "accesstoken";
        String PUSH_WAR = "pushway";
        String PAGE_NO = "pageno";
        String PAGE_SIZE = "pagesize";
        String PHONE_MODEL = "phonemodel";
        String OS = "os";
        String CONTENT = "content";
        String CONTACT = "contact";
        String MODULE_CODE = "modulecode";
        String FILE_NAME = "filename";
        String FUND_CODE = "fundcode";
        String ITEM_ID = "itemid";
        String ID_TYPE = "idtype";
        String ID_NO = "idno";
        String AD_TYPE = "adtype";  //广告类型 0 – 首页广告图  1 – 资产页广告图
        String IS_GET_MONEY_FUND ="isgetmoneyfund";// 是否过滤现金宝：0-否，1-是
        String STATE ="state";   //状态
        String QUERY_TYPE = "querytype";  //0-当日记录 ，1-历史记录；默认0
        String BUSIN_FLAG = "businflag";  //确认业务代码
        String QUERY_RANGE = "queryrange";//查询范围
        String IMG_BASE_64 = "imgbase64";
        String MONTH_SUB_STATE = "monthsubstate";
        String WEEK_SUB_STATE = "weeksubstate";
        String DEVICE_TOKEN = "devicetoken";
        String CERTIFICATE_TYPE= "certificatetype";
        String CERTIFICATE_NO= "certificateno";
        String PASSWORD= "password";
        String Q_NO_LIST = "qnolist";   //格式=1|2|3|4
        String Q_ANSWER_LIST = "qanswerlist";  //格式=A|B|C|D
        String PRE_CHECK_FLAG = "precheckflag";

        String WARNING_NO = "warningno";     //警示编号
        String BROWER_TYPE = "browertype";   //浏览器类型  不要填
        String CUST_RISK_TYPE = "custrisktype";  //投资人风险等级
        String FUND_RISK_LEVEL = "fundrisklevel";   //基金风险等级
        String MAC = "mac";
        String OPEN_ID = "openid";  //微信openid 不要填
        String PLACE_HOLDER = "placeholder";  //占位符  处理待办事项的时候要填
        String REQUEST_NO = "requestno";  //订单号  处理待办事项的时候要填
        String DEAL_TYPE = "dealtype";  //操作类型 0 – 继续使用 1 – 终止
        String SERIAL_NO = "serialno";  //待办编号
        String MOBILE_NO = "mobileno";
        String SEND_INTENT = "sendintent"; // 发送目的
        String AUTH_CODE = "authcode";
        String EMAIL = "email";
        String MARRIAGE = "marriage";
        String EDUCATIONAL = "educational";
        String ANNUAL_SALARY = "annualsalary";
        String HOLDING_NAME = "holdingname";
        String BIRTHDAY = "birthday";  //生日
        String NATIONALITY = "nationality";  //国籍
        String WORK_CODE = "workcode";  //职业
        String SEX = "sex";  //性别
        String ZIP_CODE = "zipcode";  //邮编
        String COMMUNICATION_ADDR = "communicationaddr";  //通讯地址
        String PUBHG_FLAG = "pubhgflag";  //公募合格投资者标志
        String INVALIDATE = "invalidate";  // 证件有效期
        String BENEFICIARY = "beneficiary";
        String T_PASSWD = "tpasswd";
        //未成年人才有的参数
        String TRANSACTOR_CERTI_TYPE = "transactorcertitype";  //经办人证件类型
        String TRANSACTOR_CERTI_NUMBER = "transactorcertinumber";  //经办人证件号
        String TRANSACTOR_CERTI_VALIDITY = "transactorcertivalidity";  //经办人证件有效期
        String TRANSACTOR_CONTRELATION = "transactorcontrelation";  //与经办人之间的关系
        String TRANSACTOR_NAME = "transactorname";  //经办人姓名
        String TRANSACTOR_TELEPHONE = "transactortelephone";   //经办人联系电话

        String CUST_NAME="custname";                  //客户姓名
        String BANK_SERIAL="bankserial";              //银行编码
        String BANK_ACCO="bankacco";                   //银行卡号


        String BANK_NAME = "bankname";//银行名称
        String MOBILE="mobile";//手机号码
        String IDENTITY_NO="identityno";//证件号码
        String IDENTITY_TYPE="identitytype";//证件类型
        String CUSTOMER_NAME="customername";//客户名称
        String ACCO_REQ_SERIAL="accoreqserial";//申请编号2.8.1拿到的申请编号为空，不必传，否则必传

        String CAPITAL_MODE = "capitalmode";//资金方式1:普通方式3:银联通4:工行网银6:汇付天下A:农行网银B:建行网银E:支付宝G:招行网银J:民生网银M:通联支付
        String CUSTOMER_APPELLACTION = "customerappellation";//客户名称
        String DETAIL_CAPITAL_MODE="detailcapitalmode";//00:直接网银支付;01:商户代扣;02:汇款支付;03：快捷支付  前端传参01
        String TRADE_PASSWORD = "tradepassword";//接口默认进行弱密码校验
        String YINLIAN_CD_CARD = "yinliancdcard";//如果2.7.2返回不为空，则必传
        String COMMENT_ID = "commentid";


        String RESET_TYPE = "resettype";  //重置类型 0 – 仅注册未开户 1 – 已开户
        String NEW_PWD = "newpwd"; //新密码
        String OFFSET = "offset";
        String APPLY_SUM = "applysum";//申请金额  认申购必传
        String SHARE_TYPE = "sharetype";//收费方式
        String TRADE_ACCO = "tradeacco";
        String CAP_SOURCE = "capsource";
        String BUY_FLAG = "buyflag";
        String TARGET_FUND_CODE = "targetfundcode";
        String IS_RAP_ID ="israpid";//是否快捷 1: 仅是快捷 2: 仅是非快捷 3: 快捷和非快捷

    }


    public interface Value{
        String WEEK = "0";
        String ONE_MONTH = "1";
        String TWO_MONTH = "2";
        String THREE_MONTH = "3";
        String SIX_MONTH = "6";
        String TWELVE_MONTH = "12";
        String SINCE_FOUND = "00";// 成立以来
        String REGISTER = "register";
        String BIND_MOBILE = "bindmobile";
        String RESET_PWD = "resetpwd";
        String CONTINUE_TRADE = "0";
        String END_TRADE = "1";
        String PUBHG_FLAG_1 = "1";
        //参数sendintent对应的值：openacco：开户，  open:绑定
        String SEND_INTENT_OPEN_ACCO = "";
        String SEND_INTENT_OPEN = "open";
        String RESET_PASSWORD = "RESETPASSWORD";
        String OPEN_TRADE_ACCO = "OPENTRADEACCO";
        String QUERY_TODAY = "0";
        String QUERY_HISTORY = "1";

        String RESET_JUST_REGISTER = "0"; //注册未开户
        String RESET_HAS_OPENACCO = "1";  //已开户
    }
}
