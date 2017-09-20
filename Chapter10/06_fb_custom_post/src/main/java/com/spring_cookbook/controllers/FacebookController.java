package com.spring_cookbook.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookLink;
import org.springframework.social.facebook.api.PostData;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FacebookController {
	
	private static final String APP_SECRET = "1b13515eb31b0e2b4b9c620f72761e62";
	private static final String APP_ID = "759801647423672";
	private static final String SCOPE = "user_friends, publish_actions";
	private static final String CALLBACK_URL = "http://localhost:8080/spring_webapp/fb/callback";
	private static final String TOKEN_NAME = "facebookToken";

	@RequestMapping("/fb")
	public String fb(HttpServletRequest request, Model model) {
		String accessToken = (String) request.getSession().getAttribute(TOKEN_NAME);
		
		Facebook facebook = new FacebookTemplate(accessToken);					
		if(facebook.isAuthorized()) {
			
			PostData postData = new PostData(facebook.userOperations().getUserProfile().getId());
			
			postData.message("Vegetables are good for you.");
			postData.link("http://jeromejaglale.com");
			postData.caption("Pasta and vegetables");
			postData.description("Carbs are fine. Just don't forget your vegetables.");
			postData.picture("http://jeromejaglale.com/images/photo/vancouver_summer_2007/aa_02_appetissant.JPG");
			
			facebook.feedOperations().post(postData);
		
			return "fb";
		}
		else {
			return "redirect:/fb/login";			
		}	
	}
	
	@RequestMapping("/fb/login")
	public void login(HttpServletResponse response) throws IOException {
		FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(APP_ID, APP_SECRET);

		OAuth2Parameters params = new OAuth2Parameters();
		params.setRedirectUri(CALLBACK_URL);
		params.setScope(SCOPE);

		OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
		String authorizeUrl = oauthOperations.buildAuthorizeUrl(params);
		
		response.sendRedirect(authorizeUrl);
	}
		
	@RequestMapping("/fb/callback")
	public String callback(@RequestParam("code") String authorizationCode, HttpServletRequest request) {
		FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(APP_ID, APP_SECRET);
		
		OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
		AccessGrant accessGrant = oauthOperations.exchangeForAccess(authorizationCode, CALLBACK_URL, null);

		String token = accessGrant.getAccessToken();
		request.getSession().setAttribute(TOKEN_NAME, token);
		
		return "redirect:/fb";
	}
}
