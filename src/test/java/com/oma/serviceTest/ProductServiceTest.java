package com.oma.serviceTest;

import com.oma.model.Product;
import com.oma.repository.ProductRepo;
import com.oma.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepo productRepo;

    @InjectMocks
    private ProductService productService;

    private UUID testId;
    private Product testProduct;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        testId = UUID.randomUUID();
        testProduct = new Product();
        testProduct.setId(testId);
        testProduct.setTitle("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(100.0);
        testProduct.setQuantity(10);
        testProduct.setFeatured(false);
    }

    @Test
    public void testGetAllProducts() {
        List<Product> products = Collections.singletonList(testProduct);
        when(productRepo.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals(testProduct.getTitle(), result.get(0).getTitle());
        verify(productRepo, times(1)).findAll();
    }

    @Test
    public void testFindProductById_success() {
        when(productRepo.findById(testId)).thenReturn(Optional.of(testProduct));

        Optional<Product> result = Optional.ofNullable(productService.findById(testId));

        assertTrue(result.isPresent());
        assertEquals(testProduct.getTitle(), result.get().getTitle());
        verify(productRepo, times(1)).findById(testId);
    }

    @Test
    public void testFindProductById_notFound() {
        when(productRepo.findById(testId)).thenReturn(Optional.empty());

        Optional<Product> result = Optional.ofNullable(productService.findById(testId));

        assertFalse(result.isPresent());
    }

    @Test
    public void testFindProductByStoreId() {
        when(productRepo.findByStoreId(testId)).thenReturn((List<Product>) testProduct);

        Product result = (Product) productService.findByStoreId(testId);

        assertEquals(testProduct.getTitle(), result.getTitle());
        verify(productRepo, times(1)).findByStoreId(testId);
    }

    @Test
    public void testFindProductByCategoryId() {
        when(productRepo.findByCategoryId(testId)).thenReturn((List<Product>) testProduct);

        Product result = (Product) productService.findByCategoryId(testId);

        assertEquals(testProduct.getTitle(), result.getTitle());
        verify(productRepo, times(1)).findByCategoryId(testId);
    }

    @Test
    public void testCreateProduct() {
        productService.createProduct(testProduct);
        verify(productRepo, times(1)).save(testProduct);
    }

    @Test
    public void testUpdateProduct_success() {
        Product updatedProduct = new Product();
        updatedProduct.setTitle("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setPrice(200.0);

        when(productRepo.findById(testId)).thenReturn(Optional.of(testProduct));
        when(productRepo.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.updateProduct(testId, updatedProduct);

        assertEquals(updatedProduct.getTitle(), result.getTitle());
        assertEquals(updatedProduct.getDescription(), result.getDescription());
        assertEquals(updatedProduct.getPrice(), result.getPrice());
        verify(productRepo, times(1)).findById(testId);
        verify(productRepo, times(1)).save(any(Product.class));
    }

    @Test
    public void testUpdateProduct_notFound() {
        when(productRepo.findById(testId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(testId, new Product());
        });

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    public void testDeleteProduct() {
        productService.deleteProduct(testId);
        verify(productRepo, times(1)).deleteById(testId);
    }
}
