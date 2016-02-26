package com.zlf.testdemo01.domain;

import java.io.Serializable;
import java.lang.reflect.Field;

public class MyBaseBean implements Serializable{
	protected String addString() {
		String t = "";
		Field[] fields = this.getClass().getDeclaredFields();

		for (Field field : fields) {
			try {
				// if (field.get(o) instanceof File[]) {
				// map.put(j, (File[]) field.get(o));
				// }
				field.setAccessible(true);
				if (field.get(this) != null)
					t += field.getName() + "=" + field.get(this).toString() + "|";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return t;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
