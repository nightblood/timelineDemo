package com.zlf.testdemo01.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import com.zlf.testdemo01.MainActivity;
import com.zlf.testdemo01.domain.EmotionInfo;

public class FileUtils {
	
	public static String PATH_EMOJI_TXT;
	public static String PATH_EMOJI_ZIP;
	public static String DIR_EMOJI_IMAGES; 

	public static String DIR_APPLICATION;
	/**
	 * ��ȡ�ļ����ݲ�����
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readFileByChars(String fileName) {
		File file = new File(fileName);
		String data = "";

		if (!file.exists()) {
			System.out.println("error file name!! " + fileName + "\n" + PATH_EMOJI_TXT);
			return null;
		}
		Reader reader = null;
		try {
			// System.out.println("���ַ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���ֽڣ�");
			// һ�ζ�һ���ַ�
			reader = new InputStreamReader(new FileInputStream(file));
			int tempchar;
			while ((tempchar = reader.read()) != -1) {
				// ����windows�£�\r\n�������ַ���һ��ʱ����ʾһ�����С�
				// ������������ַ��ֿ���ʾʱ���ỻ�����С�
				// ��ˣ����ε�\r����������\n�����򣬽������ܶ���С�
				if (((char) tempchar) != '\r') {
					// System.out.print((char) tempchar);
					data += (char) tempchar;
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		// System.out.print("read emotion data file: " + data);
		return data;
	}

	/**
	 * ��content����д���ļ�
	 * 
	 * @param fileName
	 * @param content
	 */
	public static void appendFile(String fileName, String content) {
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
