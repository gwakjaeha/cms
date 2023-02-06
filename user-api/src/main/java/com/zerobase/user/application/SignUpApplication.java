package com.zerobase.user.application;

import com.zerobase.user.client.MailgunClient;
import com.zerobase.user.client.mailgun.SendMailForm;
import com.zerobase.user.domain.SignUpForm;
import com.zerobase.user.domain.model.Customer;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.exception.ErrorCode;
import com.zerobase.user.service.SignUpCustomerService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SignUpApplication {
//	private final MailgunClient mailgunClient;
	private final SignUpCustomerService signUpCustomerService;
	public void customerVerify(String email, String code){
		signUpCustomerService.verifyEmail(email, code);
	}
	public String customerSignUp(SignUpForm form){
		if(signUpCustomerService.isEmailExist(form.getEmail())){
			throw new CustomException(ErrorCode.ALREADY_REGISTER_USER);
		} else {
			Customer c = signUpCustomerService.signUp(form);
			LocalDateTime now = LocalDateTime.now();

			String code = getRandomCode();

			SendMailForm sendMailForm = SendMailForm.builder()
						.from("kjh19920718@gmail.com")
						.to(form.getEmail())
						.subject("Verification Email!")
						.text(getVerificationEmailBody(form.getEmail(), form.getName(), code))
						.build();

//			mailgunClient.sendEmail(sendMailForm);
			signUpCustomerService.changeCustomerValidateEmail(c.getId(), code);
			return "회원가입에 성공하였습니다.";
		}
	}

	private String getRandomCode(){
		return RandomStringUtils.random(10, true, true);
	}

	private String getVerificationEmailBody(String email, String name, String code){
		StringBuilder builder = new StringBuilder();
		return builder.append("Hello ").append(name).append("! Please Check Link for Verification.\n\n")
			.append("http://localhost:8081/signup/verify/customer?email=")
			.append(email)
			.append("&code=")
			.append(code).toString();
	}
}
