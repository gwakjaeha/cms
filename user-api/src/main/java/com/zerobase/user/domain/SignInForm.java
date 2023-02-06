package com.zerobase.user.domain;

import lombok.Getter;
import org.springframework.boot.SpringApplicationRunListener;

@Getter
public class SignInForm {
	private String email;
	private String password;
}
