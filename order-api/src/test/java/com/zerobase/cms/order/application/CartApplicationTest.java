package com.zerobase.cms.order.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.product.AddProductCartForm;
import com.zerobase.cms.order.domain.product.AddProductForm;
import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.domain.repository.ProductRepository;
import com.zerobase.cms.order.service.ProductService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CartApplicationTest {
	@Autowired
	private CartApplication cartApplication;
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductRepository productRepository;

	@Test
	void ADD_TEST_MODIFY(){

		Long customerId = 100L;

		cartApplication.clearCart(customerId);

		Product p = add_product();
		Product result = productRepository.findWithProductItemsById(p.getId()).get();

		assertNotNull(result);

		assertEquals("나이키 에어포스", result.getName());
		assertEquals("신발", result.getDescription());

		assertEquals(3, result.getProductItems().size());
		assertEquals("나이키 에어포스0", result.getProductItems().get(0).getName());
		assertEquals(10000, result.getProductItems().get(0).getPrice());
//		assertEquals(1, result.getProductItems().get(0).getCount());

		Cart cart = cartApplication.addCart(customerId, makeAddForm(result));
		// 데이터가 잘 들어 갔는지
		assertEquals(0, cart.getMessages().size());

		cart = cartApplication.getCart(customerId);
		assertEquals(1, cart.getMessages().size());

	}

	AddProductCartForm makeAddForm(Product p){
		AddProductCartForm.ProductItem productItem =
			AddProductCartForm.ProductItem.builder()
				.id(p.getProductItems().get(0).getId())
				.name(p.getProductItems().get(0).getName())
				.count(5)
				.price(20000)
				.build();

		return AddProductCartForm.builder()
				.id(p.getId())
				.sellerId(p.getSellerId())
				.name(p.getName())
				.description(p.getDescription())
				.items(List.of(productItem))
				.build();
	}


	Product add_product(){
		Long sellerId = 1L;
		AddProductForm form = makeProductForm("나이키 에어포스", "신발", 3);
		return productService.addProduct(sellerId, form);
	}

	private static AddProductForm makeProductForm(String name, String description, int itemCount){
		List<AddProductItemForm> itemForms = new ArrayList<>();
		for (int i = 0; i < itemCount; i++) {
			itemForms.add(makeProductItemForm(null, name + i));
		}
		return AddProductForm.builder()
			.name(name)
			.description(description)
			.items(itemForms)
			.build();
	}

	private static AddProductItemForm makeProductItemForm(Long productId, String name){
		return AddProductItemForm.builder()
			.productId(productId)
			.name(name)
			.price(10000)
			.count(10)
			.build();
	}
}