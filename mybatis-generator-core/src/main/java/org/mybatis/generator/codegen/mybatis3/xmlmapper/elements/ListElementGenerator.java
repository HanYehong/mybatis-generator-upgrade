package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.apache.tools.ant.util.CollectionUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.GeneratedKey;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

/**
 * @author 韩业红
 * @date 2020/5/17
 */
public class ListElementGenerator extends
        AbstractXmlElementGenerator {
    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("select");

        answer.addAttribute(new Attribute(
                "id", introspectedTable.getListStatementId()));

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
        List<IntrospectedColumn> rowStatus = removeIdentityAndGeneratedAlwaysColumns.stream().filter(x -> "row_status".equals(x.getActualColumnName())).collect(Collectors.toList());
        if (rowStatus.size() > 0) {
            answer.addElement(new TextElement(" where row_status = 1"));
        } else {
            answer.addElement(new TextElement(" where 1 = 1"));
        }

        XmlElement insertTrimElement = new XmlElement("trim");
        insertTrimElement.addAttribute(new Attribute("suffixOverrides", ","));
        answer.addElement(insertTrimElement);
        
        for (IntrospectedColumn introspectedColumn : removeIdentityAndGeneratedAlwaysColumns) {
            if (!"row_status".equals(introspectedColumn.getActualColumnName())) {
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
