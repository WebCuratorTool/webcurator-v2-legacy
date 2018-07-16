package org.hibernate.dialect;

import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.MappingException;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.type.*;

/**
 * A dialect compatible with the H2 database.
 * 
 * @author Thomas Mueller
 *
 */
public class H2Dialect extends Dialect {

    private String querySequenceString;
    public H2Dialect() {
        super();
               
        querySequenceString = "select sequence_name from information_schema.sequences";
        try {
        	// HHH-2300
            Class constants = ReflectHelper.classForName( "org.h2.engine.Constants" );
            Integer build = (Integer)constants.getDeclaredField("BUILD_ID" ).get(null);
            int buildid = build.intValue();
            if(buildid < 32) {
                querySequenceString = "select name from information_schema.sequences";
            }
        } catch(Throwable e) {
            // ignore (probably H2 not in the classpath)
        }
        registerColumnType(Types.BOOLEAN, "boolean");
        registerColumnType(Types.BIGINT, "bigint");
        registerColumnType(Types.BINARY, "binary");
        registerColumnType(Types.BIT, "boolean");
        registerColumnType(Types.CHAR, "char($l)");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p,$s)");
        registerColumnType(Types.DOUBLE, "double");
        registerColumnType(Types.FLOAT, "float");
        registerColumnType(Types.INTEGER, "integer");
        registerColumnType(Types.LONGVARBINARY, "longvarbinary");
        registerColumnType(Types.LONGVARCHAR, "longvarchar");
        registerColumnType(Types.REAL, "real");        
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TINYINT, "tinyint");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.VARCHAR, "varchar($l)");
        registerColumnType(Types.VARBINARY, "binary($l)");
        registerColumnType(Types.NUMERIC, "numeric");
        registerColumnType(Types.BLOB, "blob");
        registerColumnType(Types.CLOB, "clob");
        
        // select topic, syntax from information_schema.help
        // where section like 'Function%' order by section, topic

//        registerFunction("abs", new StandardSQLFunction("abs"));
        registerFunction("acos", new StandardSQLFunction("acos", DoubleType.INSTANCE));
        registerFunction("asin", new StandardSQLFunction("asin", DoubleType.INSTANCE));
        registerFunction("atan", new StandardSQLFunction("atan", DoubleType.INSTANCE));
        registerFunction("atan2", new StandardSQLFunction("atan2", DoubleType.INSTANCE));
        registerFunction("bitand", new StandardSQLFunction("bitand", IntegerType.INSTANCE));
        registerFunction("bitor", new StandardSQLFunction("bitor", IntegerType.INSTANCE));
        registerFunction("bitxor", new StandardSQLFunction("bitxor", IntegerType.INSTANCE));
        registerFunction("ceiling", new StandardSQLFunction("ceiling", DoubleType.INSTANCE));
        registerFunction("cos", new StandardSQLFunction("cos", DoubleType.INSTANCE));
        registerFunction("cot", new StandardSQLFunction("cot", DoubleType.INSTANCE));
        registerFunction("degrees", new StandardSQLFunction("degrees", DoubleType.INSTANCE));
        registerFunction("exp", new StandardSQLFunction("exp", DoubleType.INSTANCE));
        registerFunction("floor", new StandardSQLFunction("floor", DoubleType.INSTANCE));
        registerFunction("log", new StandardSQLFunction("log", DoubleType.INSTANCE));
        registerFunction("log10", new StandardSQLFunction("log10", DoubleType.INSTANCE));
//        registerFunction("mod", new StandardSQLFunction("mod", IntegerType.INSTANCE));
        registerFunction("pi", new NoArgSQLFunction("pi", DoubleType.INSTANCE));
        registerFunction("power", new StandardSQLFunction("power", DoubleType.INSTANCE));
        registerFunction("radians", new StandardSQLFunction("radians", DoubleType.INSTANCE));
        registerFunction("rand", new NoArgSQLFunction("rand", DoubleType.INSTANCE));
        registerFunction("round", new StandardSQLFunction("round", DoubleType.INSTANCE));
        registerFunction("roundmagic", new StandardSQLFunction("roundmagic", DoubleType.INSTANCE));
        registerFunction("sign", new StandardSQLFunction("sign", IntegerType.INSTANCE));
        registerFunction("sin", new StandardSQLFunction("sin", DoubleType.INSTANCE));
//        registerFunction("sqrt", new StandardSQLFunction("sqrt", DoubleType.INSTANCE));
        registerFunction("tan", new StandardSQLFunction("tan", DoubleType.INSTANCE));
        registerFunction("truncate", new StandardSQLFunction("truncate", DoubleType.INSTANCE));

        registerFunction("compress", new StandardSQLFunction("compress", BinaryType.INSTANCE));
        registerFunction("expand", new StandardSQLFunction("compress", BinaryType.INSTANCE));
        registerFunction("decrypt", new StandardSQLFunction("decrypt", BinaryType.INSTANCE));
        registerFunction("encrypt", new StandardSQLFunction("encrypt", BinaryType.INSTANCE));
        registerFunction("hash", new StandardSQLFunction("hash", BinaryType.INSTANCE));

        registerFunction("ascii", new StandardSQLFunction("ascii", IntegerType.INSTANCE));
//        registerFunction("bit_length", new StandardSQLFunction("bit_length", IntegerType.INSTANCE));
        registerFunction("char", new StandardSQLFunction("char", CharacterType.INSTANCE));
        registerFunction("concat", new VarArgsSQLFunction(StringType.INSTANCE, "(", "||", ")"));
        registerFunction("difference", new StandardSQLFunction("difference", IntegerType.INSTANCE));
        registerFunction("hextoraw", new StandardSQLFunction("hextoraw", StringType.INSTANCE));
        registerFunction("lower", new StandardSQLFunction("lower", StringType.INSTANCE));
        registerFunction("insert", new StandardSQLFunction("lower", StringType.INSTANCE));
        registerFunction("left", new StandardSQLFunction("left", StringType.INSTANCE));
//        registerFunction("length", new StandardSQLFunction("length", IntegerType.INSTANCE));
//        registerFunction("locate", new StandardSQLFunction("locate", IntegerType.INSTANCE));
//        registerFunction("lower", new StandardSQLFunction("lower", StringType.INSTANCE));
        registerFunction("lcase", new StandardSQLFunction("lcase", StringType.INSTANCE));
        registerFunction("ltrim", new StandardSQLFunction("ltrim", StringType.INSTANCE));
        registerFunction("octet_length", new StandardSQLFunction("octet_length", IntegerType.INSTANCE));
        registerFunction("position", new StandardSQLFunction("position", IntegerType.INSTANCE));
        registerFunction("rawtohex", new StandardSQLFunction("rawtohex", StringType.INSTANCE));
        registerFunction("repeat", new StandardSQLFunction("repeat", StringType.INSTANCE));
        registerFunction("replace", new StandardSQLFunction("replace", StringType.INSTANCE));
        registerFunction("right", new StandardSQLFunction("right", StringType.INSTANCE));
        registerFunction("rtrim", new StandardSQLFunction("rtrim", StringType.INSTANCE));
        registerFunction("soundex", new StandardSQLFunction("soundex", StringType.INSTANCE));
        registerFunction("space", new StandardSQLFunction("space", StringType.INSTANCE));
        registerFunction("stringencode", new StandardSQLFunction("stringencode", StringType.INSTANCE));
        registerFunction("stringdecode", new StandardSQLFunction("stringdecode", StringType.INSTANCE));
//        registerFunction("substring", new StandardSQLFunction("substring", StringType.INSTANCE));
//        registerFunction("upper", new StandardSQLFunction("upper", StringType.INSTANCE));
        registerFunction("ucase", new StandardSQLFunction("ucase", StringType.INSTANCE));

        registerFunction("stringtoutf8", new StandardSQLFunction("stringtoutf8", BinaryType.INSTANCE));
        registerFunction("utf8tostring", new StandardSQLFunction("utf8tostring", StringType.INSTANCE));

        registerFunction("current_date", new NoArgSQLFunction("current_date", DateType.INSTANCE));
        registerFunction("current_time", new NoArgSQLFunction("current_time", TimeType.INSTANCE));
        registerFunction("current_timestamp", new NoArgSQLFunction("current_timestamp", TimestampType.INSTANCE));
        registerFunction("datediff", new StandardSQLFunction("datediff", IntegerType.INSTANCE));
        registerFunction("dayname", new StandardSQLFunction("dayname", StringType.INSTANCE));
        registerFunction("dayofmonth", new StandardSQLFunction("dayofmonth", IntegerType.INSTANCE));
        registerFunction("dayofweek", new StandardSQLFunction("dayofweek", IntegerType.INSTANCE));
        registerFunction("dayofyear", new StandardSQLFunction("dayofyear", IntegerType.INSTANCE));
//        registerFunction("hour", new StandardSQLFunction("hour", IntegerType.INSTANCE));
//        registerFunction("minute", new StandardSQLFunction("minute", IntegerType.INSTANCE));
//        registerFunction("month", new StandardSQLFunction("month", IntegerType.INSTANCE));
        registerFunction("monthname", new StandardSQLFunction("monthname", StringType.INSTANCE));
        registerFunction("quarter", new StandardSQLFunction("quarter", IntegerType.INSTANCE));
//        registerFunction("second", new StandardSQLFunction("second", IntegerType.INSTANCE));
        registerFunction("week", new StandardSQLFunction("week", IntegerType.INSTANCE));
//        registerFunction("year", new StandardSQLFunction("year", IntegerType.INSTANCE));

        registerFunction("curdate", new NoArgSQLFunction("curdate", DateType.INSTANCE));
        registerFunction("curtime", new NoArgSQLFunction("curtime", TimeType.INSTANCE));
        registerFunction("curtimestamp", new NoArgSQLFunction("curtimestamp", TimeType.INSTANCE));
        registerFunction("now", new NoArgSQLFunction("now", TimestampType.INSTANCE));

        registerFunction("database", new NoArgSQLFunction("database", StringType.INSTANCE));
        registerFunction("user", new NoArgSQLFunction("user", StringType.INSTANCE));

        getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);

    }

    public String getAddColumnString() {
        return "add column";
    }

    public boolean supportsIdentityColumns() {
        return true;
    }

    public String getIdentityColumnString() {
        return "generated by default as identity"; // not null is implicit
    }

    public String getIdentitySelectString() {
        return "call identity()";
    }

    public String getIdentityInsertString() {
        return "null";
    }

    public String getForUpdateString() {
        return " for update";
    }

    public boolean supportsUnique() {
        return true;
    }

    public boolean supportsLimit() {
        return true;
    }

    public String getLimitString(String sql, boolean hasOffset) {
        return new StringBuffer(sql.length() + 20).
            append(sql).
            append(hasOffset ? " limit ? offset ?" : " limit ?").
            toString();
    }
    
    public boolean bindLimitParametersInReverseOrder() {
        return true;
    }    

    public boolean bindLimitParametersFirst() {
        return false;
    }

    public boolean supportsIfExistsAfterTableName() {
        return true;
    }

	public boolean supportsPooledSequences() {
		return true;
	}

	protected String getCreateSequenceString(String sequenceName) throws MappingException {
		return "create sequence " + sequenceName;
	}


	protected String getDropSequenceString(String sequenceName) throws MappingException {
		return "drop sequence " + sequenceName;
	}

    public String getSelectSequenceNextValString(String sequenceName) {
        return "next value for " + sequenceName;
    }

    public String getSequenceNextValString(String sequenceName) {
        return "call next value for " + sequenceName;
    }

    public String getQuerySequencesString() {
        return querySequenceString;
    }

    public boolean supportsSequences() {
        return true;
    }

    public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
        return EXTRACTER;
    }

    private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {

        /**
         * Extract the name of the violated constraint from the given SQLException.
         *
         * @param sqle The exception that was the result of the constraint violation.
         * @return The extracted constraint name.
         */
        @Override
        protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
            String constraintName = null;
            // 23000: Check constraint violation: {0}
            // 23001: Unique index or primary key violation: {0}
            if(sqle.getSQLState().startsWith("23")) {
                String message = sqle.getMessage();
                int idx = message.indexOf("violation: ");
                if(idx > 0) {
                    constraintName = message.substring(idx + "violation: ".length());
                }
            }
            return constraintName;
        }

    };

    public boolean supportsTemporaryTables() {
        return true;
    }
    
    public String getCreateTemporaryTableString() {
        return "create temporary table if not exists";
    }

    public boolean supportsCurrentTimestampSelection() {
        return true;
    }
    
    public boolean isCurrentTimestampSelectStringCallable() {
        return false;
    }
    
    public String getCurrentTimestampSelectString() {
        return "call current_timestamp()";
    }    
    
    public boolean supportsUnionAll() {
        return true;
    }


	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public boolean supportsLobValueChangePropogation() {
		return false;
	}
}