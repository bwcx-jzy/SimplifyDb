package test;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.builder.impl.SQLBuilderImpl;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.util.JdbcConstants;

/**
 * Created by jiangzeyin on 2018/9/19.
 */
public class InsertTest {
    public static void main(String[] args) {
        SQLInsertStatement mySqlInsertStatement = new SQLInsertStatement();
        mySqlInsertStatement.setTableName(new SQLIdentifierExpr("sss"));
//        mySqlInsertStatement.putAttribute("ss", "sss");

//        mySqlInsertStatement.setColumnsString("sss", 1L);
        mySqlInsertStatement.addColumn(SQLBuilderImpl.toSQLExpr(1, JdbcConstants.MYSQL));

        SQLInsertStatement.ValuesClause valuesClause = new SQLInsertStatement.ValuesClause();
        valuesClause.addValue(SQLBuilderImpl.toSQLExpr(1, JdbcConstants.MYSQL));
        SQLExprParser sqlExpr = SQLParserUtils.createExprParser("?", JdbcConstants.MYSQL);
//        public static SQLExprParser createExprParser(String sql, String dbType)
        valuesClause.addValue(sqlExpr.additive());

//        valuesClause.addValue();
        mySqlInsertStatement.addValueCause(valuesClause);

        mySqlInsertStatement.addValueCause(valuesClause.clone());
        System.out.println(mySqlInsertStatement);
    }
}
