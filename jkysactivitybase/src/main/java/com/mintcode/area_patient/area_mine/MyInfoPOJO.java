package com.mintcode.area_patient.area_mine;

//import com.fasterxml.jackson.annotation.JsonTypeName;

import com.mintcode.area_patient.entity.Cpx;
import com.mintcode.area_patient.entity.Diabetes;
import com.mintcode.area_patient.entity.MyInfo;
import com.mintcode.base.BasePOJO;

import java.io.Serializable;

//@JsonTypeName(TASKID.CLT_MYINFO)
public class MyInfoPOJO extends BasePOJO {

	private static final long serialVersionUID = -4126370568901894280L;

	private MyInfo myinfo;

	private Cpx cpx;

	private Diabetes diabetes;
	/**
	 * 我的专属编码
	 */
	private QrCode qrcode;

	public MyInfo getMyinfo() {
		return myinfo;
	}

	public void setMyinfo(MyInfo myinfo) {
		this.myinfo = myinfo;
	}

	public Cpx getCpx() {
		return cpx;
	}

	public void setCpx(Cpx cpx) {
		this.cpx = cpx;
	}

	public Diabetes getDiabetes() {
		return diabetes;
	}

	public void setDiabetes(Diabetes diabetes) {
		this.diabetes = diabetes;
	}

	public QrCode getQrcode() {
		return qrcode;
	}

	public void setQrcode(QrCode qrcode) {
		this.qrcode = qrcode;
	}

	public static final class QrCode implements Serializable {
		private static final long serialVersionUID = 1L;
		/**
		 * 医生专属编码
		 */
		private String code;
		/**
		 * 二维码图片
		 */
		private String image;

		/**
		 * 邀请成功后奖励的糖币
		 */
		private int rewardCoin;

		/**
		 * 邀请提示语
		 */
		private String inviteMsg;

		/**
		 * 分享提示语
		 */
		private String shareMsg;

		/**
		 * 分享标题
		 */
		private String shareTitle;

		public String getShareTitle() {
			return shareTitle;
		}

		public void setShareTitle(String shareTitle) {
			this.shareTitle = shareTitle;
		}

		public static long getSerialVersionUID() {
			return serialVersionUID;
		}

		public String getInviteMsg() {
			return inviteMsg;
		}

		public void setInviteMsg(String inviteMsg) {
			this.inviteMsg = inviteMsg;
		}

		public String getShareMsg() {
			return shareMsg;
		}

		public void setShareMsg(String shareMsg) {
			this.shareMsg = shareMsg;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}

		public int getRewardCoin() {
			return rewardCoin;
		}

		public void setRewardCoin(int rewardCoin) {
			this.rewardCoin = rewardCoin;
		}

		public static long getSerialversionuid() {
			return serialVersionUID;
		}

	}
}
