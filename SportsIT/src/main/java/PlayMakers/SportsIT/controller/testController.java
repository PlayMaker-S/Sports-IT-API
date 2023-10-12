package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.utils.api.ApiUtils;
import PlayMakers.SportsIT.utils.api.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class testController {
    @RequestMapping("/test")
    public String test(){
        return "payment";
    }

    // Json을 반환하는 컨트롤러
    @RequestMapping("/oauth")
    public ResponseEntity<CommonResponse> oauth(@PathVariable String state, @PathVariable String code, @PathVariable String scope){

        Map<String, Object> data = new HashMap<>();
        data.put("state", state);
        data.put("code", code);
        data.put("scope", scope);

        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("message", "success");
        log.info("state: {}  code: {}  scope: {}", state, code, scope);
        return ResponseEntity.ok(ApiUtils.success(HttpStatus.OK.value(), result));
    }

}
