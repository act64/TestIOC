package com.mintcode.area_patient.entity;

import java.io.Serializable;
import java.util.List;

public class Diabetes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6342424704934225382L;

	/*
	1 一型
	2 二型
	3 妊娠
	4 其他
	5 非糖尿病
	*/
	private int diabeteType;

	private long diagnosisTime;

	private long diagnosisTime2;

	private String course;

	private int familyHistory;

	private int treatment;

	private int confirmed;

	private int withDiabetes; // 0:未确诊，1：已确诊

	private List<Complication> complication;

	private List<Symptom> symptom;

	private List<MedicationScheme> medicationSchemeList;

	private List<Checkresult> checkresultList;

	private List<Complication> complication2List;
	
	private int smoke; //0：否 1：是

	public long getDiagnosisTime2() {
		return diagnosisTime2;
	}

	public void setDiagnosisTime2(long diagnosisTime2) {
		this.diagnosisTime2 = diagnosisTime2;
	}

	public int getSmoke() {
		return smoke;
	}

	public void setSmoke(int smoke) {
		this.smoke = smoke;
	}

	public List<MedicationScheme> getMedicationSchemeList() {
		return medicationSchemeList;
	}

	public void setMedicationSchemeList(List<MedicationScheme> medicationSchemeList) {
		this.medicationSchemeList = medicationSchemeList;
	}

	public List<Checkresult> getCheckresultList() {
		return checkresultList;
	}

	public void setCheckresultList(List<Checkresult> checkresultList) {
		this.checkresultList = checkresultList;
	}

	public List<Complication> getComplication2List() {
		return complication2List;
	}

	public void setComplication2List(List<Complication> complication2List) {
		this.complication2List = complication2List;
	}

	public int getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(int confirmed) {
		this.confirmed = confirmed;
	}

	public int getWithDiabetes() {
		return withDiabetes;
	}

	public void setWithDiabetes(int withDiabetes) {
		this.withDiabetes = withDiabetes;
	}

	public int getDiabeteType() {
		return diabeteType;
	}

	public void setDiabeteType(int diabeteType) {
		this.diabeteType = diabeteType;
	}

	public long getDiagnosisTime() {
		return diagnosisTime;
	}

	public void setDiagnosisTime(long diagnosisTime) {
		this.diagnosisTime = diagnosisTime;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public int getFamilyHistory() {
		return familyHistory;
	}

	public void setFamilyHistory(int familyHistory) {
		this.familyHistory = familyHistory;
	}

	public int getTreatment() {
		return treatment;
	}

	public void setTreatment(int treatment) {
		this.treatment = treatment;
	}

	public List<Complication> getComplication() {
		return complication;
	}

	public void setComplication(List<Complication> complication) {
		this.complication = complication;
	}

	public List<Symptom> getSymptom() {
		return symptom;
	}

	public void setSymptom(List<Symptom> symptom) {
		this.symptom = symptom;
	}

	public static class Symptom implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5563873893041153333L;

		private int symId;

		private String symName;

		public int getSymId() {
			return symId;
		}

		public void setSymId(int symId) {
			this.symId = symId;
		}

		public String getSymName() {
			return symName;
		}

		public void setSymName(String symName) {
			this.symName = symName;
		}

	}

	public static class GlucoseCtl implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3423072072510417253L;
		/*
		 * id 配置id int 必要 level 等级 string 必要 优, 良, 中, 差 color 颜色 string 必要 RGB，如
		 * #32CD32 desc 描述 string
		 */
		private int id;
		private String level;
		private String color;
		private String desc;
		private String comment;

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getLevel() {
			return level;
		}

		public void setLevel(String level) {
			this.level = level;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

	}

	public static class Complication implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2725924836600548031L;
		/*
		 * code 代码 string 必要 name 名称 string 必要 type 分类 int 必要 1:急性2:慢性 level
		 * 急性：1：轻度，2：中度，3：重度 慢性：1：1期，2：2期，3：3期
		 */
		private String code;
		private String name;
		private int type;
		private int level;
		private String levelName;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public String getLevelName() {
			return levelName;
		}

		public void setLevelName(String levelName) {
			this.levelName = levelName;
		}
	}

	public static class BaseDrug implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7297518414077616040L;
		/*
		 * code 药品编码 string 非必要 name 药品名称 string 必要 type 药品类型 string 必要 胰岛素,
		 * 降糖药, ... unit 计量单位 string 必要 毫升, 片, ...
		 */
		private String code;
		private String name;
		private String type;
		private String unit;
		private int times;
		private int amount;
		private String unitname;

		public String getUnitname() {
			return unitname;
		}

		public void setUnitname(String unitname) {
			this.unitname = unitname;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public int getTimes() {
			return times;
		}

		public void setTimes(int times) {
			this.times = times;
		}

		public int getAmount() {
			return amount;
		}

		public void setAmount(int amount) {
			this.amount = amount;
		}

	}

	public static class Drug extends BaseDrug implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2626902943814263833L;
		// only extends
	}

	public static class Exercise implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1144227930604739823L;

		private int id;
		private String sportsName;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getSportsName() {
			return sportsName;
		}

		public void setSportsName(String sportsName) {
			this.sportsName = sportsName;
		}

	}

	public static class SportsRate implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8226375772411787379L;
		private int id;
		private String sportsRate;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getSportsRate() {
			return sportsRate;
		}

		public void setSportsRate(String sportsRate) {
			this.sportsRate = sportsRate;
		}

	}

	public static class Diet implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4528343859740723053L;
		private int id;
		private String dietName;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getDietName() {
			return dietName;
		}

		public void setDietName(String dietName) {
			this.dietName = dietName;
		}
	}

	public static class MedicationScheme implements Serializable {

		/**
		 * code 代码 string 必要 参数字典参见:2.5.1 获取生活处方配置 name 名称 string 必要 unit 单位
		 * string 必要 unitname 单位描述 string 必要 600毫克/片 type 类型 string 必要 此这字段可不用
		 * number 名称 string 必要 药品剂量 time 名称 string 必要 服药时间 remark 名称 string 必要
		 * 备注
		 */
		private static final long serialVersionUID = 4528343859740723053L;
		private String code;
		private String unit;
		private String type;
		private String number;
		private String unitname;
		private String time;
		private String remark;
		private String name;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String getUnitname() {
			return unitname;
		}

		public void setUnitname(String unitname) {
			this.unitname = unitname;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public static class Checkresult implements Serializable {

		/**
		 * categoryName 检查项目录名称 string 必要 参数字典参见:2.5.8 获取健康指导配置数据
		 * suggestCheckItemList 检查项列表 list<suggestCheckItem> 必要
		 */
		private static final long serialVersionUID = 4528343859740723053L;
		private String categoryName;
		private String categoryCode;
		private String categoryUrl;
		private List<SuggestCheckItem> suggestCheckItemList;

		public String getCategoryName() {
			return categoryName;
		}

		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}

		public List<SuggestCheckItem> getSuggestCheckItemList() {
			return suggestCheckItemList;
		}

		public void setSuggestCheckItemList(
				List<SuggestCheckItem> suggestCheckItemList) {
			this.suggestCheckItemList = suggestCheckItemList;
		}

		public String getCategoryCode() {
			return categoryCode;
		}

		public void setCategoryCode(String categoryCode) {
			this.categoryCode = categoryCode;
		}

		public String getCategoryUrl() {
			return categoryUrl;
		}

		public void setCategoryUrl(String categoryUrl) {
			this.categoryUrl = categoryUrl;
		}
	}

	public static class SuggestCheckItem implements Serializable {

		/**
		 * content 检查项名称 string 必要 unit 检查项单位 string 必要 value 检查项值 string 必要
		 */
		private static final long serialVersionUID = 4528343859740723053L;
		private String content;
		private String unit;
		private String value;
		private String code;
		private boolean isTitle = false;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public boolean isTitle() {
			return isTitle;
		}

		public void setTitle(boolean isTitle) {
			this.isTitle = isTitle;
		}

	}

}
