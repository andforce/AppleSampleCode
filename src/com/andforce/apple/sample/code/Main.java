package com.andforce.apple.sample.code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

	private static final String sBaseUrl = "https://developer.apple.com/library/content";
	private static final String sLibrary = "https://developer.apple.com/library/content/navigation/library.json";
	private static final String sBook = "https://developer.apple.com/library/content/samplecode/%s/book.json";

	private static final String sZipUrl = "https://developer.apple.com/library/content/samplecode/%s/%s";
	
	private static final String formTitle = "|  名称  | 平台&下载 |  最后更新时间  |\n"
										  + "| ----- |  -----   |   -----:   |\n";
	private static final String form = 		"|   %s  |    %s    |     %s     |\n";
	
	private static final String sIntroFormat = "[%s](%s)";
	private static final String sDownloadFormat = "[%s](%s)";
	
	private static final String sMDHead = "# AppleSampleCode\nMirror of [Apple Sample Code](https://developer.apple.com/library/content/navigation/index.html#section=Resource%20Types&amp;topic=Sample%20Code)\n\n";
	
	public static void main(String[] args) {
		String libraryJson = getLibraryJson(sLibrary);

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
				String fullIntroUrl = introUrl.replace("..", sBaseUrl);
				
				
				String bookJson = getLibraryJson(String.format(sBook, zip));
				if (!bookJson.startsWith("{")) {
					continue;
				}

				JSONObject book = new JSONObject(bookJson);
				
				if (book.has("sampleCode")) {
					String sampleCode = book.getString("sampleCode");
					
					System.out.println(sampleCode + "\n\n");
					
					String nameAndIntro = String.format(sIntroFormat, codeName, fullIntroUrl);
					String platformAndDownload = String.format(sDownloadFormat, platform, sampleCode);
					String oneLine = String.format(form, nameAndIntro, platformAndDownload, lastVersionDate);
					markdown.append(oneLine);
					size ++;
				} else {
					continue;
				}

			}
		}
		
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
			
			File tmpFile = new File("tmp.json");
			tmpFile.delete();
			System.exit(0);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getLibraryJson(String url) {
		Process process = null;
		StringBuffer stringBuffer = new StringBuffer();
		try {
			process = Runtime.getRuntime().exec("rm -rf tmp.json");

			try {
				if (process.waitFor() == 0) {
					process = Runtime.getRuntime().exec("curl -o tmp.json " + url);
					
					if (process.waitFor() == 0) {
						File file = new File("tmp.json");
						
						BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
						String line = "";
						while ((line = input.readLine()) != null) {
							stringBuffer.append(line);
							System.out.println("URL : " +url);
							System.out.println("->> " +line);
						}
						input.close();
					}

				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuffer.toString().trim();
	}
}
