package com.zerobase.cms.order.domain.repository;

import com.zerobase.cms.order.domain.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

	@EntityGraph(attributePaths = {"productItems"}, type = EntityGraphType.LOAD)
	Optional<Product> findBySellerIdAndId(Long SellerId, Long id);

	//Lazy 로 설정하면 테스트시 ProductItems 를 안불러와서 이 부분 추가
	@EntityGraph(attributePaths = {"productItems"}, type = EntityGraphType.LOAD)
	Optional<Product> findWithProductItemsById(Long id);

	@EntityGraph(attributePaths = {"productItems"}, type = EntityGraphType.LOAD)
	List<Product> findAllByIdIn(List<Long> ids);
}
