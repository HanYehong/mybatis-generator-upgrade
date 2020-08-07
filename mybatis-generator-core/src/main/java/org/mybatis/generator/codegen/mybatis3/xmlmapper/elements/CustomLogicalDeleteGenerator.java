package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 逻辑删除生成器
 * <update>
 *     update table_name set row_status = 0 where ……
 * </update>
 *
 * @author 韩业红
 * @date 2020/5/17
 */
public class CustomLogicalDeleteGenerator extends
        AbstractXmlElementGenerator {
    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("update");

        answer.addAttribute(new Attribute("id", "remove"));

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append("update ");

        answer.addElement(new TextElement(sb.toString()));

        sb.setLength(0);
        sb.append(introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        List<IntrospectedColumn> removeIdentityAndGeneratedAlwaysColumns =
                ListUtilities.removeIdentityAndGeneratedAlwaysColumns(
                        introspectedTable.getAllColumns());
        List<IntrospectedColumn> rowStatus = removeIdentityAndGeneratedAlwaysColumns.stream().filter(x -> "row_status".equals(x.getActualColumnName())).collect(Collectors.toList());
        if (rowStatus.size() > 0) {
            answer.addElement(new TextElement("set row_status = 0"));
        } else {
            answer.addElement(new TextElement("set @empty"));
        }

        XmlElement insertTrimElement = new XmlElement("trim");
        insertTrimElement.addAttribute(new Attribute("suffixOverrides", ","));
        answer.addElement(insertTrimElement);

        List<String> notGenColumnNames = Arrays.asList("row_status", "update_by", "update_id", "update_on", "create_by", "creator_id", "create_on", "tenant_code");

        for (IntrospectedColumn introspectedColumn : removeIdentityAndGeneratedAlwaysColumns) {
            if (!notGenColumnNames.contains(introspectedColumn.getActualColumnName())) {
                gen(sb, insertTrimElement, introspectedColumn);
            }
        }

        if (context.getPlugins()
                .sqlMapSelectByPrimaryKeyElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }

    private void gen(StringBuilder sb, XmlElement insertTrimElement, IntrospectedColumn introspectedColumn) {
        sb.setLength(0);

        sb.append(introspectedColumn.getJavaProperty());
        sb.append(" != null");
        XmlElement valuesNotNullElement = new XmlElement("if");
        valuesNotNullElement.addAttribute(new Attribute(
                "test", sb.toString()));


        sb.setLength(0);
        sb.append(" and ")
                .append(introspectedColumn.getActualColumnName())
                .append(" = ")
                .append("#{").append(introspectedColumn.getJavaProperty()).append("}");

        sb.append(',');
        valuesNotNullElement.addElement(new TextElement(sb.toString()));

        insertTrimElement.addElement(valuesNotNullElement);
    }
}
