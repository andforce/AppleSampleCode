package com.andforce.apple.sample.code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

	private static final String sBaseUrl = "https://developer.apple.com/library/content";
	private static final String sLibrary = "https://developer.apple.com/library/content/navigation/library.json";

	private static final String sZipUrl = "https://developer.apple.com/library/content/samplecode/%s/%s.zip";
	
	private static final String formTitle = "| 名称 | 平台 | 说明 | 下载 | 最后更新时间 |\n| ----- | ----- | ----- | ----- | -----: |\n";
	private static final String form = 		"| %s  |  %s  |  %s |  %s |     %s     |\n";
	
	private static final String sIntroFormat = "[说明](%s)";
	private static final String sDownloadFormat = "[Source Code](%s)";
	
	private static final String sMDHead = "# AppleSampleCode\nMirror of [Apple Sample Code]https://developer.apple.com/library/content/navigation/index.html#section=Resource%20Types&amp;topic=Sample%20Code\n\n";
	
	public static void main(String[] args) {
		String libraryJson = getLibraryJson();
		JSONObject jsonObject = new JSONObject(libraryJson);
		JSONArray documents = (JSONArray) jsonObject.get("documents");
		
		int documentCount = documents.length();

		StringBuilder markdown = new StringBuilder(formTitle);
		int size = 0;
		for (int i = 0; i < documentCount; i++) {
			JSONArray doc = documents.getJSONArray(i);
			int sourceType = (int) doc.get(2);
			if (sourceType == 5) {
				String codeName = doc.getString(0);
				String lastVersionDate = doc.getString(3);
				String introUrl = doc.getString(9);
				String platform = doc.getString(12).replace("|", "\\|");
				String zip = introUrl.split("/")[2];
				String downloadZipUrl = String.format(sZipUrl, zip, zip);
				String fullIntroUrl = introUrl.replace("..", sBaseUrl);
				
				String oneLine = String.format(form, codeName, platform, String.format(sIntroFormat, fullIntroUrl), String.format(sDownloadFormat, downloadZipUrl), lastVersionDate);
				markdown.append(oneLine);
				size ++;
			}
		}
		
		System.out.println(size);
		
		System.out.println(markdown);
		
		try {
			String data = sMDHead + markdown.toString();

			File file = new File("README.md");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// true = append file
			FileWriter fileWritter = new FileWriter(file.getName(), false);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(data);
			bufferWritter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getLibraryJson() {
		Process process = null;
		StringBuffer stringBuffer = new StringBuffer();
		try {
			process = Runtime.getRuntime().exec("curl " + sLibrary);
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = input.readLine()) != null) {
				stringBuffer.append(line);
			}
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuffer.toString();
	}
}
