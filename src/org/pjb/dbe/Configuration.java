package org.pjb.dbe;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class Configuration {
	private static final Configuration instance = new Configuration("dbe.properties");
	public static final String KEY_DRIVER = "driver";
	public static final String KEY_URL = "url";
	public static final String KEY_CATALOG = "catalog";
	public static final String KEY_SCHEMA = "schema";
	public static final String KEY_USERID = "userid";
	public static final String KEY_PASSWORD = "passwd";
	public static final String KEY_EXPORT_DDL = "export.ddl";
	public static final String KEY_EXPORT_DDL_VIEWS = "export.ddl.views";
	public static final String KEY_EXPORT_DDL_NATIVE = "export.ddl.native";
	public static final String KEY_EXPORT_DATA = "export.data";
	public static final String KEY_EXPORT_DATA_DELETES = "export.data.deletes";
	public static final String KEY_TABLES_FILTERS = "filter.tables";
	public static final String KEY_ORACLE_TO_MYSQL = "export.ddl.to.mysql";
	public static final String KEY_ORACLE_TO_MYSQL_OPTS_CREATE = "export.ddl.to.mysql.options.create";

	public static final String KEY_MYSQL_TO_ORACLE = "export.ddl.to.oracle";
	public static final String DRIVER_ORACLE_THIN = "oracle.thin";
	public static final String DRIVER_MYSQL = "mysql";
	private String path;
	private Map<String, String> cache = new HashMap<String, String>();

	public Configuration(String path) {
		this.path = path;
		loadProperties();
	}

	public static final Configuration getInstance() {
		return instance;
	}

	private void loadProperties() {
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(new File(this.path)));

			@SuppressWarnings("unchecked")
			Enumeration<String> propertyNames = (Enumeration<String>) properties.propertyNames();
			while (propertyNames.hasMoreElements()) {
				String key = propertyNames.nextElement();
				if (System.getProperty(key) != null)
					this.cache.put(key, System.getProperty(key));
				else if (System.getenv(key) != null)
					this.cache.put(key, System.getenv(key));
				else
					this.cache.put(key, properties.getProperty(key));
			}
		} catch (IOException e) {
			throw new RuntimeException("@Configuration.loadProperties / could not load properties '" + this.path + "'");
		}
	}

	public List<String> findStringKeys(String prefix) {
		List<String> list = new ArrayList<String>();
		for (Map.Entry<String, String> entry : this.cache.entrySet()) {
			if ((entry.getKey()).startsWith(prefix)) {
				list.add(entry.getKey());
			}
		}
		return list;
	}

	public String getStringValue(String key) {
		return (String) this.cache.get(key);
	}

	public String getStringValue(String key, String defaultValue) {
		String value = (String) this.cache.get(key);
		return StringUtils.isNotEmpty(value) ? value : defaultValue;
	}

	public boolean getBooleanValue(String key, boolean defaultValue) {
		String value = getStringValue(key);
		if (StringUtils.isNotEmpty(value)) {
			return Boolean.TRUE.toString().equals(value.toLowerCase());
		}
		return defaultValue;
	}

	public int getIntegerValue(String key, int defaultValue) {
		String value = getStringValue(key);
		if (StringUtils.isNotEmpty(value)) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new RuntimeException("@Configuration.getIntegerValue / bad properties value format in file '"
						+ this.path + "', key='" + key + "', value='" + value + "'");
			}
		}
		return defaultValue;
	}

	public float getFloatValue(String key, float defaultValue) {
		String value = getStringValue(key);
		if (StringUtils.isNotEmpty(value)) {
			try {
				return Float.parseFloat(value);
			} catch (NumberFormatException e) {
				throw new RuntimeException("@Configuration.getFloatValue / bad properties value format in file '"
						+ this.path + "', key='" + key + "', value='" + value + "'");
			}
		}
		return defaultValue;
	}
}