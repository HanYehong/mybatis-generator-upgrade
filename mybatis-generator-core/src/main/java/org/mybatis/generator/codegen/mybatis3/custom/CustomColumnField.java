package org.mybatis.generator.codegen.mybatis3.custom;

import java.util.Arrays;
import java.util.List;

/**
 * @author 韩业红 WeChat：autumn6980
 * @date 2020/8/7
 */
public class CustomColumnField {
	public static final String ROW_STATUS = "row_status";
	public static final String UPDATE_ID = "update_id";
	public static final String UPDATE_BY = "update_by";
	public static final String UPDATE_ON = "update_on";
	public static final String CREATOR_ID = "creator_id";
	public static final String CREATE_BY = "create_by";
	public static final String CREATE_ON = "create_on";
	public static final String TENANT_CODE = "tenant_code";
	public static final List<String> NOT_GEN_COLUMN_NAMES = Arrays.asList(
			ROW_STATUS, UPDATE_BY, UPDATE_ID, UPDATE_ON,
			CREATE_BY, CREATOR_ID, CREATE_ON, TENANT_CODE);

	public static boolean inFields(String field) {
		return NOT_GEN_COLUMN_NAMES.contains(field);
	}
}
