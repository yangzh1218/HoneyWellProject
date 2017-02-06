/**
 * Project Name:  WulianLibrary
 * File Name:     WeekModel.java
 * Package Name:  com.wulian.siplibrary.model
 * @Date:         2014年11月25日
 * Copyright (c)  2014, wulian All Rights Reserved.
*/

package com.wulian.siplibrary.model.linkagedetection;
/**
 * @ClassName: WeekModel
 * @Function:  一周时间模型
 * @Date:      2014年11月25日
 * @author     Puml
 * @email      puml@wuliangroup.cn
 */
public enum WeekModel {
	MONDAY("Mon"),//星期一
	TUESDAY("Tues"),//星期二
	WEDNESDAY("Wed"),//星期三
	THURSDAY("Thurs"),//星期四
	FRIDAY("Fri"),//星期五
	SATURDAY("Sat"),//星期六
	SUNDAY("Sun");//星期天
	String dayName;
	private WeekModel (String dayName) {
		this.dayName=dayName;
	}
	
	public String getDayName() {
		return this.dayName;
	}
	
}

