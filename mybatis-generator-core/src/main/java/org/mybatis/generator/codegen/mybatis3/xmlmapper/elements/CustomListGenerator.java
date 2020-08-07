package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.custom.CustomColumnField;
import org.mybatis.generator.config.TableConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 条件查询生成器
 *
 * @author 韩业红
 * @date 2020/5/17
 */
public class CustomListGenerator extends
        AbstractXmlElementGenerator {
    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("select");

        answer.addAttribute(new Attribute(
                "id", "list"));

        if (introspectedTable.getRules().generateResultMapWithBLOBs()) {
            answer.addAttribute(new Attribute("resultMap",
                    introspectedTable.getResultMapWithBLOBsId()));
        } else {
            answer.addAttribute(new Attribute("resultMap",
                    introspectedTable.getBaseResultMapId()));
        }

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append("select ");

        answer.addElement(new TextElement(sb.toString()));
        answer.addElement(getBaseColumnListElement());

        sb.setLength(0);
        sb.append("from ");
        sb.append(introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        List<IntrospectedColumn> removeIdentityAndGeneratedAlwaysColumns =
                ListUtilities.removeIdentityAndGeneratedAlwaysColumns(
                        introspectedTable.getAllColumns());
        List<IntrospectedColumn> rowStatus = removeIdentityAndGeneratedAlwaysColumns.stream().filter(
                x -> CustomColumnField.ROW_STATUS.equals(x.getActualColumnName())).collect(Collectors.toList());
        if (rowStatus.size() > 0) {
            answer.addElement(new TextElement("where " + CustomColumnField.ROW_STATUS + " = 1"));
        } else {
            answer.addElement(new TextElement("where 1 = 1"));
        }

        XmlElement insertTrimElement = new XmlElement("trim");
        insertTrimElement.addAttribute(new Attribute("suffixOverrides", ","));
        answer.addElement(insertTrimElement);

        for (IntrospectedColumn introspectedColumn : removeIdentityAndGeneratedAlwaysColumns) {
            if (!CustomColumnField.inFields(introspectedColumn.getActualColumnName())) {
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


        List<TableConfiguration> tableConfigurations = context.getTableConfigurations();
        TableConfiguration tableConfiguration = tableConfigurations.get(0);
        List<String> foreachFields = tableConfiguration.getForeachFields();

        if (foreachFields.contains(introspectedColumn.getActualColumnName())) {
            sb.setLength(0);
            sb.append(" and ")
                    .append(introspectedColumn.getActualColumnName())
                    .append(" in ");

            XmlElement foreachElement = new XmlElement("foreach");
            foreachElement.addAttribute(new Attribute("collection", "list"));
            foreachElement.addAttribute(new Attribute("item", "item"));
            foreachElement.addAttribute(new Attribute("open", "("));
            foreachElement.addAttribute(new Attribute("close", ")"));
            foreachElement.addAttribute(new Attribute("separator", ","));
            foreachElement.addElement(new TextElement(
                    MyBatis3FormattingUtilities
                            .getParameterClause(introspectedColumn, "item.")));

            valuesNotNullElement.addElement(new TextElement(sb.toString()));
            valuesNotNullElement.addElement(foreachElement);
        } else {
            sb.setLength(0);
            sb.append(" and ")
                    .append(introspectedColumn.getActualColumnName())
                    .append(" = ")
                    .append(MyBatis3FormattingUtilities
                            .getParameterClause(introspectedColumn));
            valuesNotNullElement.addElement(new TextElement(sb.toString()));
        }

        insertTrimElement.addElement(valuesNotNullElement);
    }
}
