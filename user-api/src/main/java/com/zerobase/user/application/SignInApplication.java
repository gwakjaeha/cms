package com.zerobase.user.application;

import static com.zerobase.user.exception.ErrorCode.LOGIN_CHECK_FAIL;

import com.zerobase.domain.common.UserType;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import com.zerobase.user.domain.SignInForm;
import com.zerobase.user.domain.model.Customer;
import com.zerobase.user.domain.model.Seller;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.service.customer.CustomerService;
import com.zerobase.user.service.seller.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInApplication {
	private final CustomerService customerService;
	private final SellerService sellerService;
	private final JwtAuthenticationProvider provider;

	public String customerLoginToken(SignInForm form){
		Customer c = customerService.findValidCustomer(form.getEmail(), form.getPassword())
			.orElseThrow(()->new CustomException(LOGIN_CHECK_FAIL));
		return provider.createToken(c.getEmail(), c.getId(), UserType.CUSTOMER);
	}

	public String sellerLoginToken(SignInForm form){
		Seller s = sellerService.findValidSeller(form.getEmail(), form.getPassword())
			.orElseThrow(()->new CustomException(LOGIN_CHECK_FAIL));
		return provider.createToken(s.getEmail(), s.getId(), UserType.SELLER);
	}
}
