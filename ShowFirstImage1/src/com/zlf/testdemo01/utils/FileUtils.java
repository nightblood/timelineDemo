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
	 * 读取文件内容并返回
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
			// System.out.println("以字符为单位读取文件内容，一次读一个字节：");
			// 一次读一个字符
			reader = new InputStreamReader(new FileInputStream(file));
			int tempchar;
			while ((tempchar = reader.read()) != -1) {
				// 对于windows下，\r\n这两个字符在一起时，表示一个换行。
				// 但如果这两个字符分开显示时，会换两次行。
				// 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
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
	 * 将content内容写入文件
	 * 
	 * @param fileName
	 * @param content
	 */
	public static void appendFile(String fileName, String content) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
