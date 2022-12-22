package br.com.content4devs.service.impl;

import br.com.content4devs.dto.ProductInputDTO;
import br.com.content4devs.dto.ProductOutputDTO;
import br.com.content4devs.exception.ProductAlreadyExistsException;
import br.com.content4devs.exception.ProductNotFoundException;
import br.com.content4devs.repository.ProductRepository;
import br.com.content4devs.service.IProductService;
import br.com.content4devs.util.ProductConverterUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements IProductService {

    private final ProductRepository repository;

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public ProductOutputDTO create(ProductInputDTO inputDTO) {
        this.checkDuplicity(inputDTO.getName());

        var product = ProductConverterUtil.productInputDtoToProduct(inputDTO);
        var productCreated = this.repository.save(product);
        return ProductConverterUtil.productToProductOutputDto(productCreated);
    }

    @Override
    public ProductOutputDTO findById(Long id) {
        var product = repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        return ProductConverterUtil.productToProductOutputDto(product);
    }

    @Override
    public void delete(Long id) {
        var product = repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        this.repository.delete(product);
    }

    @Override
    public List<ProductOutputDTO> findAll() {
        var products = repository.findAll();
        return products.stream().map(ProductConverterUtil::productToProductOutputDto).collect(Collectors.toList());
    }

    private void checkDuplicity(String name) {
        repository.findByNameIgnoreCase(name)
                .ifPresent(e -> {
                    throw new ProductAlreadyExistsException(name);
                });
    }
}
