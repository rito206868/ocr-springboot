package com.ocr.service;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import com.asprise.ocr.Ocr;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocr.model.OcrRequest;
import com.ocr.model.OcrResponse;

@Component
public class OcrImplService {

	private static Logger logger = LogManager.getLogger(OcrImplService.class);
	final ObjectMapper mapper = new ObjectMapper();

	/**
	 * The implementation method for OCR data extraction
	 * 
	 * @param ocrRequest
	 * @return
	 * @throws Exception
	 */
	public OcrResponse performOCR(OcrRequest ocrRequest) throws Exception {

		// start the engine
		Ocr.setUp();
		// create a new OCR engine
		Ocr ocr = new Ocr();

		logger.info("###########  Inside implementation method ###########");

		// OCI Asprise JAVA API call
		ocr.startEngine("eng", Ocr.SPEED_FASTEST); // English
		String extText = ocr.recognize(
				new File[] { new File(
						"C://ContainerCrush//KYCDocs//" + ocrRequest.getToken() + "." + ocrRequest.getType()) },
				Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_PLAINTEXT);

		// Stop the engine
		ocr.stopEngine();

		if (extText.contains("PrintLetterBarcodeData")) {
			// Invoke inland adhaar process
			return ocrProcessInLandAdhar(extText);

		} else {
			// Invoke eadhhar process
			return ocrProcessEadhar(extText);
		}

	}

	/**
	 * 
	 * The method to extract data from OCR record in case of in land adhhar
	 * 
	 * @param extText
	 * @return OcrResponse
	 * @throws ParseException
	 */
	private OcrResponse ocrProcessInLandAdhar(String extText) throws ParseException {

		final OcrResponse ocrResponse = new OcrResponse();

		// Tuning result output STARTS
		int startString = extText.indexOf("<PrintLetterBarcodeData");
		int endString = 0;
		if (extText.contains("/>]]")) {
			endString = extText.indexOf("/>]]");
		} else {
			endString = extText.indexOf(">]]");
		}
		String result = extText.substring(startString + 1, endString);
		result = result.replace("PrintLetterBarcodeData", "");

		Pattern p = Pattern.compile("(^\\s{1}[a-zA-Z]*=|\\s[a-zA-Z]*=)");
		Matcher m = p.matcher(result);
		int count = 0;
		while (m.find()) {
			String stringtoUpdated = m.group(1);
			stringtoUpdated = stringtoUpdated.trim();
			stringtoUpdated = stringtoUpdated.replace("=", "");
			if (count == 0) {
				stringtoUpdated = '"' + stringtoUpdated + '"' + ":";
			} else {
				stringtoUpdated = "," + '"' + stringtoUpdated + '"' + ":";
			}
			result = result.replace(m.group(1), stringtoUpdated);
			count++;

		}
		result = "{" + result + "}";
		result = result.replace("/", "-");
		// Tuning result output ENDS

		// Create JSON object from json string
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(result);

		// Retrieve attribute values from JSON objet
		ocrResponse.setName((String) json.get("name"));
		ocrResponse.setDob((String) json.get("dob"));
		ocrResponse.setUid((String) json.get("uid"));
		return ocrResponse;

	}

	/**
	 * 
	 * The method to extract data from OCR record in case of electronic adhaar
	 * 
	 * @param extText
	 * @return OcrResponse
	 * @throws ParseException
	 */
	private OcrResponse ocrProcessEadhar(String extText) throws ParseException {

		final OcrResponse ocrResponse = new OcrResponse();

		// Tuning result output STARTS
		StringBuilder stringJson = new StringBuilder();

		String resultSet[] = extText.split("\n");

		String dobdata[] = resultSet[4].split(":");
		resultSet[4] = dobdata[1].replace(" ", "");
		resultSet[4] = resultSet[4].replace("/", "-");

		if (resultSet[6].contains("_") && resultSet[6].contains(" ")) {
			resultSet[6] = resultSet[6].replace("_", "");
			resultSet[6] = resultSet[6].replace(" ", "");
		} else if (resultSet[6].contains(" ")) {
			resultSet[6] = resultSet[6].replace(" ", "");
		} else if (resultSet[6].contains("_")) {
			resultSet[6] = resultSet[6].replace("_", "");
		}

		stringJson.append("{" + '"' + "name" + '"' + ":" + '"' + resultSet[3] + '"' + ",");
		stringJson.append('"' + "dob" + '"' + ":" + '"' + resultSet[4] + '"' + ",");
		stringJson.append('"' + "uid" + '"' + ":" + '"' + resultSet[6] + '"' + "}");
		// Tuning result output ENDS

		// Create JSON object from json string
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(stringJson.toString());

		// Retrieve attribute values from JSON objet
		ocrResponse.setName((String) json.get("name"));
		ocrResponse.setDob((String) json.get("dob"));
		ocrResponse.setUid((String) json.get("uid"));

		return ocrResponse;

	}

}
