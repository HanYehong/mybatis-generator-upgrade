package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.GeneratedKey;

import java.util.List;

/**
 * @author 韩业红
 * @date 2020/5/17
 */
public class SaveBatchElementGenerator extends
        AbstractXmlElementGenerator {
    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("insert");

        answer.addAttribute(new Attribute(
                "id", introspectedTable.getSaveBatchStatementId()));

        context.getCommentGenerator().addComment(answer);

        GeneratedKey gk = introspectedTable.getGeneratedKey();
        if (gk != null) {
            IntrospectedColumn introspectedColumn = introspectedTable
                    .getColumn(gk.getColumn());

            if (introspectedColumn != null) {
                if (gk.isJdbcStandard()) {
                    answer.addAttribute(new Attribute("useGeneratedKeys", "true"));
                    answer.addAttribute(new Attribute("keyProperty", introspectedColumn.getJavaProperty()));
                    answer.addAttribute(new Attribute("keyColumn", introspectedColumn.getActualColumnName()));
                } else {
                    answer.addElement(getSelectKey(introspectedColumn, gk));
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("insert into ");
        stringBuilder.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(stringBuilder.toString()));

        XmlElement insertTrimElement = new XmlElement("trim");
        insertTrimElement.addAttribute(new Attribute("prefix", "("));
        insertTrimElement.addAttribute(new Attribute("suffix", ") values "));
        insertTrimElement.addAttribute(new Attribute("suffixOverrides", ","));
        answer.addElement(insertTrimElement);


        for (IntrospectedColumn introspectedColumn : ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable
                .getAllColumns())) {

            stringBuilder.setLength(0);
            stringBuilder.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn))
                    .append(",");
            insertTrimElement.addElement(new TextElement(stringBuilder.toString()));
        }

        XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("collection", "list"));
        foreachElement.addAttribute(new Attribute("item", "item"));
        foreachElement.addAttribute(new Attribute("separator", ","));
        answer.addElement(foreachElement);

        XmlElement insertValueElement = new XmlElement("trim");
        insertValueElement.addAttribute(new Attribute("prefix", "("));
        insertValueElement.addAttribute(new Attribute("suffix", ")"));
        insertValueElement.addAttribute(new Attribute("suffixOverrides", ","));
        foreachElement.addElement(insertValueElement);

        List<IntrospectedColumn> removeIdentityAndGeneratedAlwaysColumns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable
                .getAllColumns());
        for (IntrospectedColumn introspectedColumn : removeIdentityAndGeneratedAlwaysColumns) {

            stringBuilder.setLength(0);
            stringBuilder.append("item.").append(introspectedColumn.getJavaProperty());
            stringBuilder.append(" != null");
            XmlElement valuesNotNullElement = new XmlElement("when");
            valuesNotNullElement.addAttribute(new Attribute(
                    "test", stringBuilder.toString()));

            stringBuilder.setLength(0);
            stringBuilder.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn, "item."));
            stringBuilder.append(',');
            valuesNotNullElement.addElement(new TextElement(stringBuilder.toString()));

            stringBuilder.setLength(0);
            XmlElement valuesIsNullElement = new XmlElement("otherwise");
            String defaultValue = introspectedColumn.getDefaultValue();
            if (defaultValue == null) {
                defaultValue = "";
            }
            stringBuilder
                    .append("'")
                    .append(defaultValue)
                    .append("'");
            stringBuilder.append(',');
            valuesIsNullElement.addElement(new TextElement(stringBuilder.toString()));

            XmlElement chooseElement = new XmlElement("choose");
            chooseElement.addElement(valuesNotNullElement);
            chooseElement.addElement(valuesIsNullElement);
            insertValueElement.addElement(chooseElement);
        }

        if (context.getPlugins().sqlMapInsertSelectiveElementGenerated(
                answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
