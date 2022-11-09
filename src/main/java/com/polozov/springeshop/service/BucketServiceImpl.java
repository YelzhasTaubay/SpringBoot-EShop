package com.polozov.springeshop.service;

import com.polozov.springeshop.dao.BucketRepository;
import com.polozov.springeshop.dao.ProductRepository;
import com.polozov.springeshop.domain.Bucket;
import com.polozov.springeshop.domain.Product;
import com.polozov.springeshop.domain.User;
import com.polozov.springeshop.dto.BucketDTO;
import com.polozov.springeshop.dto.BucketDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Вместо Автовайринга
public class BucketServiceImpl implements BucketService{

    private final BucketRepository bucketRepository;
    private final ProductRepository productRepository;
    private final UserService userService;



    @Override
    @Transactional
    public Bucket createBucket(User user, List<Long> productIds) {
        Bucket bucket=new Bucket();
        bucket.setUser(user);
        List<Product> productList=getCollectRefProductByIds(productIds);
        bucket.setProducts(productList);
        return bucketRepository.save(bucket);
    }

    private List<Product> getCollectRefProductByIds(List<Long> productIds){
        //getOne вытаскивает ссылку на обьект, findById вытаскивает сам обьект
        return productIds.stream().map(productRepository :: getOne)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addProduct(Bucket bucket, List<Long> productIds) {
        List<Product> products=bucket.getProducts();
        List<Product> newProductsList = products ==  null ? new ArrayList<>() : new ArrayList<>(products);
        newProductsList.addAll(getCollectRefProductByIds(productIds));
        bucket.setProducts(newProductsList);
        bucketRepository.save(bucket);
    }

    @Override
    public BucketDTO getBucketByUser(String name) {
        User user=userService.findByName(name);

        if (user == null || user.getBucket() == null){
            return new BucketDTO();
        }

        BucketDTO bucketDTO=new BucketDTO();
        Map<Long, BucketDetailDTO> mapByProductId=new HashMap<>();

        List<Product> products=user.getBucket().getProducts();
        for (Product product : products){
            BucketDetailDTO detail=mapByProductId.get(product.getId());
            if (detail == null){
                mapByProductId.put(product.getId(), new BucketDetailDTO(product));
            }else {
//                detail.setAmount(detail.getAmount()+1.0);
//                detail.setSum(detail.getSum()+product.getPrice());
            }
        }
        bucketDTO.setBucketDetails(new ArrayList<>(mapByProductId.values()));
        bucketDTO.aggregate();
        return bucketDTO;
    }


}
