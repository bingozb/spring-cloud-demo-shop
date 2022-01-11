package com.bhnote.controller;


import com.bhnote.model.ProductDO;
import com.bhnote.model.ro.LockProductRequest;
import com.bhnote.model.ro.SaveProductRequest;
import com.bhnote.model.vo.ProductVO;
import com.bhnote.service.ProductService;
import com.bhnote.utils.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author Bingo
 * @since 2022-01-07
 */
@Api(tags = "商品模块")
@RestController
@RequestMapping("/api/product/v1")
public class ProductController {

    @Resource
    private ProductService productService;

    @ApiOperation("保存商品")
    @PostMapping("save")
    public JsonData saveProduct(@ApiParam("保存商品请求体") @RequestBody SaveProductRequest saveProductRequest) {
        return productService.saveProduct(saveProductRequest);
    }

    @ApiOperation("获取商品列表")
    @GetMapping("list")
    public JsonData getPageProduct(@ApiParam("页数") @RequestParam(defaultValue = "1") long page,
                                   @ApiParam("条数") @RequestParam(defaultValue = "10") long size) {
        return productService.getPageProduct(page, size);
    }

    @ApiOperation("获取商品详情")
    @GetMapping("/detail/{product_id}")
    public JsonData getProductDetail(@ApiParam(value = "商品ID", required = true) @PathVariable(value = "product_id") long productId) {
        return productService.getProductDetail(productId);
    }

    @ApiOperation("RPC-批量获取指定的商品信息")
    @PostMapping("batch_list")
    public List<ProductVO> getBatchProduct(@ApiParam(value = "商品ID数组", required = true) @RequestBody List<Long> productIds) {
        return productService.getBatchProduct(productIds);
    }

    @ApiOperation("RPC-锁定商品库存")
    @PostMapping("lock")
    public JsonData lockProductStock(@ApiParam("商品库存锁定请求体") @RequestBody LockProductRequest lockProductRequest) {
        return productService.lockProductStock(lockProductRequest);
    }
}

