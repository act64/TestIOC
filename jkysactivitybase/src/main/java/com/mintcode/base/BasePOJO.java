package com.mintcode.base;

//import com.fasterxml.jackson.annotation.JsonSubTypes;
//import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.google.gson.Gson;
import com.mintcode.area_patient.area_login.VerifyCodePOJO;

import java.io.Serializable;

/**以action 的string作为唯一识别码，需要在对应的Bean类里面对应的写
 * 例如：@JsonTypeName("verify-code")
 * 参考{@link VerifyCodePOJO}
 * */
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "action")//
//@JsonSubTypes({
//		@JsonSubTypes.Type(value = VerifyCodePOJO.class),
//		@JsonSubTypes.Type(value = NewVerifyCodePOJO.class),
//		@JsonSubTypes.Type(value = TaskListPOJO.class),
//		@JsonSubTypes.Type(value = TaskRewardPOJO.class),
//		@JsonSubTypes.Type(value = UpdateSugarPOJO.class),
//		@JsonSubTypes.Type(value = LogoutPOJO.class),
//		@JsonSubTypes.Type(value = LoginPOJO.class),
//		@JsonSubTypes.Type(value = LoginPasswordPOJO.class),
//		@JsonSubTypes.Type(value = SysconfReportPOJO.class),
//		@JsonSubTypes.Type(value = GetSugarDataPOJO.class),
//		@JsonSubTypes.Type(value = MyInfoPOJO.class),
//		@JsonSubTypes.Type(value = DrInfoPOJO.class),
//		@JsonSubTypes.Type(value = GlucoseLimitsPOJO.class),
//		@JsonSubTypes.Type(value = ProvincePOJO.class),
//		@JsonSubTypes.Type(value = DoctorListPOJO.class),
//		@JsonSubTypes.Type(value = DoctorDetailPOJO.class),
//		@JsonSubTypes.Type(value = MyCoinPOJO.class),
//		@JsonSubTypes.Type(value = ListReportPOJO.class),
//		@JsonSubTypes.Type(value = CheckinPOJO.class),
//		@JsonSubTypes.Type(value = WechatPOJO.class),
//		@JsonSubTypes.Type(value = WechatLoginPOJO.class),
//		@JsonSubTypes.Type(value = AttachDetail.class),
//		@JsonSubTypes.Type(value = AlarmPOJO.class),
//		@JsonSubTypes.Type(value = ChangeMobilePOJO.class),
//		@JsonSubTypes.Type(value = ValidationAppverPOJO.class),
//		@JsonSubTypes.Type(value = HomePOJO.class),
//		@JsonSubTypes.Type(value = GetUserPOJO.class),
//		@JsonSubTypes.Type(value = OrderListPOJO.class),
//		@JsonSubTypes.Type(value = GiftListPOJO.class),
//		@JsonSubTypes.Type(value = CommitOrderPOJO.class),
//		@JsonSubTypes.Type(value = CheckInfoPOJO.class),
//		@JsonSubTypes.Type(value = CstServicePOJO.class),
//		@JsonSubTypes.Type(value = CheckinRankPOJO.class),
//		@JsonSubTypes.Type(value = ServicePOJO.class),
//		@JsonSubTypes.Type(value = AddressPOJO.class),
//		@JsonSubTypes.Type(value = ServiceDescPOJO.class),
//		@JsonSubTypes.Type(value = HealthOptionsPOJO.class),
//		@JsonSubTypes.Type(value = MyShareFoodPOJO.class),
//		@JsonSubTypes.Type(value = ShareFoodPOJO.class),
//		@JsonSubTypes.Type(value = MyFavoritePOJO.class),
//		@JsonSubTypes.Type(value = ShareFoodDetailPOJO.class),
//		@JsonSubTypes.Type(value = CommentPOJO.class),
//		@JsonSubTypes.Type(value = HealthGuidePOJO.class),
//		@JsonSubTypes.Type(value = CommentListPOJO.class),
//		@JsonSubTypes.Type(value = DietaryDetailPOJO.class),
//		@JsonSubTypes.Type(value = DietaryListPOJO.class),
//		@JsonSubTypes.Type(value = CheckIn_20.class),
//		@JsonSubTypes.Type(value = ReportPOJO.class),
//		@JsonSubTypes.Type(value = DietaryRelateFoodListPOJO.class),
//		@JsonSubTypes.Type(value = TicklingPOJO.class),
//		@JsonSubTypes.Type(value = MsgUnreadCountPOJO.class),
//		@JsonSubTypes.Type(value = MsgListPOJO.class),
//		@JsonSubTypes.Type(value = CheckIn_30.class),
//		@JsonSubTypes.Type(value = MyInfoShopPOJO.class),
//
//})
public class BasePOJO implements Serializable {

//	private static SparseArray<String> NextActionMap = new SparseArray<String>();
//
//	public static String getActionMessage(int code) {
//		return NextActionMap.get(code);
//	}
//
//	private static SparseArray<String> SuccessMap = new SparseArray<String>();
//
//	public static String getSuccessMessage(int code) {
//		return SuccessMap.get(code);
//	}
//
//	private static SparseArray<String> TryAgainMap = new SparseArray<String>();
//
//	public static String getTryAgainMessage(int code) {
//		return TryAgainMap.get(code);
//	}
//
//	private static SparseArray<String> LogoutMap = new SparseArray<String>();
//
//	public static String getLogoutMessage(int code) {
//		return LogoutMap.get(code);
//	}
//
//	private static SparseArray<String> PleasePay = new SparseArray<String>();
//
//	public static String getPayMessage(int code) {
//		return PleasePay.get(code);
//	}
//
//	static {
//		NextActionMap.put(1000, "请绑定手机号");
//	}
//	static {
//		SuccessMap.put(2000, "操作成功");
//		SuccessMap.put(2100, "医发送验证码");
//		SuccessMap.put(2200, "审核待反馈");
//	}
//	static {
//		TryAgainMap.put(3000, "请重试");
//		TryAgainMap.put(3100, "手机号格式不正确");
//		TryAgainMap.put(3101, "发送验证码失败");
//		TryAgainMap.put(3102, "验证码无效");
//	}
//	static {
//		LogoutMap.put(4000, "请重新登录");
//		LogoutMap.put(4100, "Token过期");
//	}
//	static {
//		PleasePay.put(5000, "请付费");
//	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String action;

	private int code;

	private String message;

	private String rsaPublicKey;

	public String getRsaPublicKey() {
		return rsaPublicKey;
	}

	public void setRsaPublicKey(String rsaPublicKey) {
		this.rsaPublicKey = rsaPublicKey;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isResultSuccess() {
		return code / 1000 == 2;
	}

	public boolean isLogout(){
		return code / 1000 == 4;
	}
//	public String getDetailMsg(int code) {
//		String error = "";
//		int type = code / 1000;
//		switch (type) {
//		case 1:
//			error = getActionMessage(code);
//			break;
//		case 2:
//			error = getSuccessMessage(code);
//			break;
//		case 3:
//			error = getTryAgainMessage(code);
//			break;
//		case 4:
//			error = getLogoutMessage(code);
//			break;
//		case 5:
//			error = getPayMessage(code);
//			break;
//		default:
//			break;
//		}
//		return error;
//	}

	public String toJson(){
		return new Gson().toJson(this);
	}
}
