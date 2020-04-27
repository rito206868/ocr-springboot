package com.ocr.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocr.model.OcrRequest;
import com.ocr.model.OcrResponse;
import com.ocr.service.OcrImplService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/digitalbanking")
public class OcrFileController {

	private static Logger logger = LogManager.getLogger(OcrFileController.class);
	final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	OcrImplService ocrImplService;

	/**
	 * This controller is written for OCR data extraction
	 * @param jsonBody
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/{id}/KYC", produces = APPLICATION_JSON_VALUE)
	public @ResponseBody OcrResponse getKYC(@PathVariable final Long id) throws Exception {
		
		//Request object creation to call service method
		final OcrRequest ocrRequest = new OcrRequest(String.valueOf(id)+"_adhar", "jpg");

		logger.info(">>>>>> ocrDataExtraction process begins  :: ", ocrRequest.getToken());

		//Calling OCR implementation service
		return ocrImplService.performOCR(ocrRequest);

	}

}
