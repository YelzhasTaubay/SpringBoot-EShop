package com.polozov.springeshop.controllers;

import com.polozov.springeshop.dto.ProductDTO;
import com.polozov.springeshop.service.ProductService;
import com.polozov.springeshop.service.SessionObjectHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {


    private final ProductService productService;
    private SessionObjectHolder sessionObjectHolder;
    @Autowired
    public ProductController(ProductService productService,SessionObjectHolder sessionObjectHolder) {
        this.productService = productService;
        this.sessionObjectHolder=sessionObjectHolder;
    }

    @GetMapping
    public String list(Model model){
        sessionObjectHolder.addClick();
        List<ProductDTO> list=productService.getAll();
        model.addAttribute("products",list);
        return "products";
    }

    @GetMapping(value = "/{id}/bucket")
    public String addBucket(@PathVariable Long id, Principal principal){
        sessionObjectHolder.addClick();
        if (principal == null){
            return "redirect:/products";
        }else {
            productService.addToUserBucket(id,principal.getName());
            return "redirect:/products";
        }
    }

}
