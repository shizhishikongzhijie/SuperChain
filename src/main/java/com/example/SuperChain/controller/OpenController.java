package com.example.SuperChain.controller;

import com.example.SuperChain.bean.User;
import com.example.SuperChain.util.ProofUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/open")
public class OpenController {
    @GetMapping ("/newAccount")
    public Boolean test(String pk , String sk) throws JsonProcessingException {
        log.info("pk:{},sk:{}",pk,sk);
        pk = "H4sIAAAAAAAAAHXBwRFCMQgFwHZ0JgfIAwLlhAT6L+Ef9Ki7L+AYL40jNsvmukdkq3lr5l3CoqHVu3Vy5exSqKAKhMViiKEuewk8KnzazuhO85wkpujLtMRDkVZQz0DwPlzR9zq4jAaP/qIPpj/6t/N+AO8tTQvGAAAA";
        sk = "96160616365861245542405055015282740270824890055593060139554048302137181520596";
        return ProofUtil.register(new User(pk,sk));
    }

}
