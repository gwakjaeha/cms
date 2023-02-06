package com.zerobase.user.service.customer;

import com.zerobase.user.domain.SignUpForm;
import com.zerobase.user.domain.model.Customer;
import com.zerobase.user.domain.repository.CustomerRepository;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {

	private final CustomerRepository customerRepository;

	public Customer signUp(SignUpForm form){
		return customerRepository.save(Customer.from(form));
	}

	public boolean isEmailExist(String email){
		return customerRepository.findByEmail(email.toLowerCase(Locale.ROOT))
			.isPresent();
	}

	@Transactional
	public void verifyEmail(String email, String code){
		Customer customer = customerRepository.findByEmail(email)
			.orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_USER));
		if(customer.isVerify()){
			throw new CustomException(ErrorCode.ALREADY_VERIFY);
		} else if(!customer.getVerificationCode().equals(code)){
			throw new CustomException(ErrorCode.WRONG_VERIFICATION);
		} else if(customer.getVerifyExpiredAt().isBefore(LocalDateTime.now())){
			throw new CustomException(ErrorCode.EXPIRE_CODE);
		}
		customer.setVerify(true);
	}

	@Transactional //save 안해도 바뀐 부분 저장해줌.
	public LocalDateTime changeCustomerValidateEmail(Long customerId, String verficationCode){
		Optional<Customer> customerOptional = customerRepository.findById(customerId);

		if(customerOptional.isPresent()){
			Customer customer = customerOptional.get();
			customer.setVerificationCode(verficationCode);
			customer.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));
			return customer.getVerifyExpiredAt();
		}
		throw new CustomException(ErrorCode.NOT_FOUND_USER);
	}
}
