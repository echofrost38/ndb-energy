package com.ndb.auction.hooks;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.ndb.auction.models.User;
import com.ndb.auction.models.sumsub.Applicant;
import com.ndb.auction.models.sumsub.ApplicantResponse;
import com.ndb.auction.models.sumsub.Review;
import com.ndb.auction.payload.ReviewResult;
import com.ndb.auction.payload.SumsubPayload;
import com.ndb.auction.service.SumsubService;

@RestController
@RequestMapping("/")
public class SumsubController extends BaseController{
	
	@Value("{sumsub.webhook.secret}")
	private String SECRET;
	
	@PostMapping("/sumsub")
	@ResponseBody
	public ResponseEntity<?> SumsubWebhooks(HttpServletRequest request) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
		
		String hmac = request.getHeader("x-payload-digest");
		
		String reqQuery = "";
		try {
			reqQuery = getBody(request);
		} catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		String _hmac = buildHmacSHA1Signature(reqQuery, SECRET);
		
		if(!hmac.equals(_hmac)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		SumsubPayload payload = new Gson().fromJson(reqQuery, SumsubPayload.class);
		ReviewResult result = payload.getReviewResult();
		
		if(result == null) {		
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		}
		
		if(result.getReviewAnswer().equals("GREEN")) {
			String applicantId = payload.getApplicantId();
			Applicant app = sumsubService.getApplicant(applicantId);
			
			if(app == null) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			
			ApplicantResponse applicantData = sumsubService.gettingApplicantData(applicantId);
			Review review = applicantData.getReview();
			String levelName = review.getLevelName();			
			
			if(!review.getReviewResult().getReviewAnswer().equals("GREEN")) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			
			String userId = app.getUserId();
			User user = userService.getUserById(userId);
			
			if(levelName.equals(SumsubService.KYC)) {
				user.getSecurity().replace("KYC", true);
			} else if(levelName.equals(SumsubService.AML)) {
				user.getSecurity().replace("AML", true);
			} else {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			userService.updateUser(user);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
