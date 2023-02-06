package com.zerobase.user.service.seller;

import static com.zerobase.user.exception.ErrorCode.NOT_FOUND_USER;

import com.zerobase.user.domain.SignUpForm;
import com.zerobase.user.domain.model.Seller;
import com.zerobase.user.domain.repository.SellerRepository;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerService {

	private final SellerRepository sellerRepository;

	public Optional<Seller> findByIdAndEmail(Long id, String email){
		return sellerRepository.findByIdAndEmail(id, email);
	}
	public Optional<Seller> findValidSeller(String email, String password){
		return sellerRepository.findByEmailAndPasswordAndVerifyIsTrue(email, password);
	}
	public Seller signUp(SignUpForm form){
		return sellerRepository.save(Seller.from(form));
	}
	public boolean isEmailExist(String email){
		return sellerRepository.findByEmail(email).isPresent();
	}

	@Transactional
	public void verifyEmail(String email, String code){
		Seller seller = sellerRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER));
		if(seller.isVerify()){
			throw new CustomException(ErrorCode.ALREADY_VERIFY);
		} else if(!seller.getVerificationCode().equals(code)){
			throw new CustomException(ErrorCode.WRONG_VERIFICATION);
		} else if(seller.getVerifyExpiredAt().isBefore(LocalDateTime.now())){
			throw new CustomException(ErrorCode.EXPIRE_CODE);
		}
		seller.setVerify(true);
	}

	@Transactional //save 안해도 바뀐 부분 저장해줌.
	public LocalDateTime changeSellerValidateEmail(Long customerId, String verficationCode){
		Optional<Seller> sellerOptional = sellerRepository.findById(customerId);

		if(sellerOptional.isPresent()){
			Seller seller = sellerOptional.get();
			seller.setVerificationCode(verficationCode);
			seller.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));
			return seller.getVerifyExpiredAt();
		}
		throw new CustomException(ErrorCode.NOT_FOUND_USER);
	}
}
