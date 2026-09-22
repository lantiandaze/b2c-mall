package com.b2cmall.shop.web;
import com.b2cmall.common.Result;
import com.b2cmall.shop.service.ShopService;
import com.b2cmall.shop.web.request.ShopRegisterRequestVO;
import com.b2cmall.shop.web.response.ShopRegisterResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
@RestController
@RequestMapping("/shop")
public class ShopController {
    private final ShopService service;
    private final com.b2cmall.shop.adapter.ShopRegisterAdapter adapter;
    public ShopController(ShopService service,com.b2cmall.shop.adapter.ShopRegisterAdapter adapter) {this.service=service;this.adapter=adapter;}
    @PostMapping("/register")
    public ResponseEntity<Result<ShopRegisterResponse>> register(@Valid @RequestBody ShopRegisterRequestVO request) {
        return response(service.register(adapter.convert(request)));
    }
    @PostMapping("/v2/register")
    public ResponseEntity<Result<ShopRegisterResponse>> registerV2(@Valid @RequestBody com.b2cmall.shop.web.request.ShopRegisterV2RequestVO request) {
        return response(service.register(adapter.convert(request)));
    }
    private ResponseEntity<Result<ShopRegisterResponse>> response(ShopRegisterResponse result) {
        if ("COMPLETED".equals(result.state)) return ResponseEntity.ok(Result.success(result));
        if ("FAILED".equals(result.state)) return ResponseEntity.status(503).body(new Result<>(503,"初始化失败，请稍后重试",result));
        return ResponseEntity.accepted().body(new Result<>(202,"已受理，请查询初始化状态",result));
    }
    // Credentials in the POST body avoid exposing passwords in query strings.
    @PostMapping("/registration/status")
    public Result<ShopRegisterResponse> status(@Valid @RequestBody ShopRegisterRequestVO request) {
        return Result.success(service.status(adapter.convert(request)));
    }
}
