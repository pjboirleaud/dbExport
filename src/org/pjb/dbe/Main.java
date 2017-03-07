package org.pjb.dbe;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pjb.dbe.data.DataGeneratorFactory;
import org.pjb.dbe.ddl.DdlGeneratorFactory;

public class Main {
	private static String driver;
	private static String url;
	private static String catalog;
	private static String schema;
	private static String userid;
	private static String password;
	private static Connection jdbcConnection;
	private static List<String> tables_filters;
	private static List<String> tables;
	private static List<String> views;

	public static void main(String[] args) {
		try {
			if (!checkConfiguration()) {
				System.out.println("Program aborted due to errors in configuration.");
				System.exit(1);
				return;
			}

			if (!dbConnect()) {
				System.out.println("Program aborted due to errors connecting to the DB.");
				System.exit(2);
				return;
			}

			loadFilters();

			if (!listTables()) {
				System.out.println("Program aborted due to errors listing tables.");
				System.exit(3);
				return;
			}

			if (!listViews()) {
				System.out.println("Program aborted due to errors listing views.");
				System.exit(4);
				return;
			}

			if (Configuration.getInstance().getBooleanValue("export.ddl", true)) {
				if (Configuration.getInstance().getBooleanValue("export.ddl.native", false))
					DdlGeneratorFactory.getDdlGenerator().generateNativeDdl(jdbcConnection, catalog, schema, tables,
							views);
				else {
					DdlGeneratorFactory.getDdlGenerator().generateDdl(jdbcConnection, catalog, schema, tables, views);
				}

			}

			if (Configuration.getInstance().getBooleanValue("export.data", true))
				DataGeneratorFactory.getDataGenerator().generateData(jdbcConnection, catalog, schema, tables);
		} catch (Exception e) {
			System.out.println("Program aborted due to unknown errors.");
			e.printStackTrace();
			System.exit(64);
			return;
		}
	}

	private static boolean listTables() {
		try {
			DatabaseMetaData md = jdbcConnection.getMetaData();
			String[] types = { "TABLE" };
			ResultSet rs = md.getTables(catalog, schema, "%", types);
			tables = new ArrayList<String>();

			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				if ((tables.contains(tableName)) || (tables_filters.contains(tableName.toUpperCase()))) {
					continue;
				}
				tables.add(tableName);
			}

			rs.close();
		} catch (Exception e) {
			System.out.println("Error: could not retrieve table metadata from the backend.");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static boolean listViews() {
		try {
			DatabaseMetaData md = jdbcConnection.getMetaData();
			String[] types = { "VIEW" };
			ResultSet rs = md.getTables(catalog, schema, "%", types);
			views = new ArrayList<String>();

			while (rs.next()) {
				String viewName = rs.getString("TABLE_NAME");
				if ((views.contains(viewName)) || (tables_filters.contains(viewName.toUpperCase()))) {
					continue;
				}
				views.add(viewName);
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("Error: could not retrieve view metadata from the backend.");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static void loadFilters() {
		tables_filters = new ArrayList<String>();
		String f = Configuration.getInstance().getStringValue("filter.tables");
		if (StringUtils.isEmpty(f)) {
			return;
		}
		String[] tf = f.split("\\|");
		for (String s : tf)
			tables_filters.add(StringUtils.trim(s).toUpperCase());
	}

	private static boolean dbConnect() {
		try {
			jdbcConnection = DriverManager.getConnection(url, userid, password);
		} catch (SQLException e) {
			System.out.println("Error: could not connect to the backend.");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static boolean checkConfiguration() {
		boolean ok = true;
		driver = StringUtils.trim(Configuration.getInstance().getStringValue("driver"));
		url = StringUtils.trim(Configuration.getInstance().getStringValue("url"));
		catalog = StringUtils.trim(Configuration.getInstance().getStringValue("catalog"));
		schema = StringUtils.trim(Configuration.getInstance().getStringValue("schema"));
		userid = StringUtils.trim(Configuration.getInstance().getStringValue("userid"));
		password = StringUtils.trim(Configuration.getInstance().getStringValue("passwd"));

		if (StringUtils.isEmpty(driver)) {
			System.out.println("Error: missing driver configuration.");

			ok = false;
		}
		if ((!"mysql".equalsIgnoreCase(driver)) && (!"oracle.thin".equalsIgnoreCase(driver))) {
			System.out.println(
					"Error: wrong driver driver=" + driver + ". Supported = " + "mysql" + ", " + "oracle.thin");
			ok = false;
		}
		if (StringUtils.isEmpty(url)) {
			System.out.println("Error: missing url configuration.");

			ok = false;
		}
		if (StringUtils.isEmpty(userid)) {
			System.out.println("Error: missing userid configuration.");

			ok = false;
		}
		return ok;
	}
}