package org.terifan.sql;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;


public class CreateEntityClassesFromDatabase
{
	public static void create(Connection aConnection, String aDatabase, File aOutputPath) throws SQLException, IOException
	{
		HashSet<String> skipTables = new HashSet<>();
		skipTables.add("activity");

		HashSet<String> autokeyColumnNames = new HashSet<>();
		autokeyColumnNames.add("id");

		try (ResultSet tables = aConnection.getMetaData().getTables(aDatabase, "", "", null))
		{
			while (tables.next())
			{
				if ("TABLE".equals(tables.getObject("TABLE_TYPE")))
				{
					if (skipTables.contains(tables.getString("TABLE_NAME")))
					{
						continue;
					}

					String tableName = toJavaTableName(tables.getString("TABLE_NAME"));

					StringBuilder toStringBuffer = new StringBuilder();
					StringBuilder methodBuffer = new StringBuilder();
					StringBuilder fieldsBuffer = new StringBuilder();

					HashSet<String> doneColumns = new HashSet<>();
					boolean hasPrimaryKey = false;

					try (ResultSet columns = aConnection.getMetaData().getColumns(aDatabase, "", tables.getString("TABLE_NAME"), null))
					{
						while (columns.next())
						{
							String columnName = columns.getString("COLUMN_NAME");
							String javaName = toJavaName(columnName);

							if (!doneColumns.add(javaName))
							{
								javaName = columnName;
							}

							String javaType = toJavaType(columns);

//									System.out.print(tableName+" "+javaName);
//									for (int i = 1; i <= columns.getMetaData().getColumnCount(); i++)
//									{
//										System.out.print("\t" + columns.getMetaData().getColumnLabel(i)+"="+columns.getObject(i));
//									}
//									System.out.println();

							boolean key = columns.getString("IS_AUTOINCREMENT").equals("YES") || autokeyColumnNames.contains(columnName);
							hasPrimaryKey |= key;

							fieldsBuffer.append("\t" + (key ? "@PrimaryKey" : "@Column") + "(name = \"" + columnName + "\") protected " + javaType + " " + javaName + ";\n");

							toStringBuffer.append(" + \"" + (toStringBuffer.length() > 0 ? ", " : "") + javaName + "=\" + " + javaName);

							methodBuffer.append("\n\tpublic " + javaType + " get" + capFirst(javaName) + "()\n\t{\n\t\treturn " + javaName + ";\n\t}\n\n\n");
							methodBuffer.append("\tpublic " + tableName + " set" + capFirst(javaName) + "(" + javaType + " a" + capFirst(javaName) + ")\n\t{\n\t\t"+javaName+" = a"+capFirst(javaName)+";\n\t\treturn this;\n\t}\n\n");
						}

						toStringBuffer.append(" + \"}\";\n\t}");
						toStringBuffer.insert(0, "\t@Override\n\tpublic String toString()\n\t{\n\t\treturn \"" + tableName + "{\"");

						if (!hasPrimaryKey)
						{
							System.out.println("Warning: table " + tableName + " has no key");
						}
					}

					try (PrintWriter out = new PrintWriter(new File(aOutputPath, tableName + ".java")))
					{
						out.println("package tms;");
						out.println("");
						out.println("import java.util.Date;");
						out.println("import org.terifan.sql.Column;");
						out.println("import org.terifan.sql.PrimaryKey;");
						out.println("import org.terifan.sql.Table;");
						out.println("import java.io.Serializable;");
						out.println("import org.terifan.sql.EntityManager;");
						out.println();
						out.println();
						out.println("@Table(name = \"" + tables.getString("TABLE_NAME") + "\")");
						out.println("public class " + tableName + " implements Serializable");
						out.println("{");
						out.println("\tprivate final static long serialVersionUID = 1L;");
						out.println();
						out.println("\tprivate transient EntityManager __em;");
						out.println();
						out.println(fieldsBuffer.toString());
						out.println();
						out.println(methodBuffer.toString());
						out.println(toStringBuffer.toString());
						out.println("}");
					}
				}
			}
		}
	}


	private static String capFirst(String aName)
	{
		return aName.substring(1);
	}


	private static String toJavaType(ResultSet aColumns) throws SQLException
	{
		String type = aColumns.getString("TYPE_NAME");

		boolean nullable = aColumns.getInt("NULLABLE") != DatabaseMetaData.columnNoNulls;

		switch (type)
		{
			case "BIT":
				return nullable ? "Boolean" : "boolean";
			case "FLOAT":
			case "DOUBLE":
			case "DECIMAL":
				return nullable ? "Double" : "double";
			case "TINYINT":
			case "SMALLINT":
			case "MEDIUMINT":
			case "INT":
				return nullable ? "Integer" : "int";
			case "BIGINT":
				return nullable ? "Long" : "long";
			case "DATETIME":
				return "Date";
			case "CHAR":
			case "VARCHAR":
			case "TINYTEXT":
			case "TEXT":
			case "MEDIUMTEXT":
			case "LONGTEXT":
				return "String";
			case "BINARY":
			case "VARBINARY":
			case "TINYBLOB":
			case "BLOB":
			case "MEDIUMBLOB":
			case "LONGBLOB":
				return "byte[]";
			default:
				throw new IllegalArgumentException(type + " " + aColumns.getObject("COLUMN_NAME"));
		}
	}


	private static String toJavaName(String aName)
	{
		StringBuilder tmp = new StringBuilder("m");

		for (String part : aName.replace("__", "_").split("_"))
		{
			tmp.append(Character.toUpperCase(part.charAt(0)) + (part.length() <= 1 ? "" : part.substring(1)));
		}

		return tmp.toString();
	}


	private static String toJavaTableName(String aName)
	{
		StringBuilder tmp = new StringBuilder();

		if (aName.startsWith("tbl_"))
		{
			aName = aName.substring(4);
		}
		else if (aName.startsWith("t_"))
		{
			aName = aName.substring(2);
		}

		for (String part : aName.replace("__", "_").split("_"))
		{
			tmp.append(Character.toUpperCase(part.charAt(0)) + (part.length() <= 1 ? "" : part.substring(1)));
		}

		return tmp.toString();
	}
}
