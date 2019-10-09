package com.csg.snake.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class Config extends Properties {

	private static final long serialVersionUID = 1L;

	File file;

	public Config() {
		String path = System.getProperty("user.home") + File.separator + "GridSnake" + File.separator;
		File dir = new File(path);

		file = new File(path + "Config" + ".properties");

		InputStream is = null;

		try {
			if(dir.exists() || dir.mkdirs()) {
				if(file.exists() || file.createNewFile()) {
					is = new FileInputStream(file);
					load(is);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void set(String key, Object value) {
		setProperty(key, value.toString());
		try {
			store(new FileOutputStream(file), "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void add(String key, String value, Member setter, boolean removing) {
		ArrayList<String> v = getList(key);
		if(!removing)
			v.add(value.toString().toLowerCase());
		else
			v.remove(value.toString().toLowerCase());

		set(key, v.toString().trim());
	}

	public String get(String key) {
		if(getProperty(key) != null)
			return getProperty(key);
		return "";
	}

	public ArrayList<String> getList(String key) {
		if(get(key) != null && get(key).contains("[")
				&& get(key).contains("]"))
			return new ArrayList<String>(Arrays.asList(
					get(key).replace("[", "").replace("]", "")
					.split(", ")));
		return new ArrayList<String>();
	}

	public void removeKey(String key) {
		try {
			FileOutputStream o = new FileOutputStream(file);
			remove(key);
			store(o, null);
			o.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}