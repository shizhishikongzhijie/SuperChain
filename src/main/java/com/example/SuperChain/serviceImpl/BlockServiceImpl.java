package com.example.SuperChain.serviceImpl;

import com.alibaba.fastjson2.JSONObject;
import com.example.SuperChain.config.XchainConfig;
import com.example.SuperChain.service.BlockService;

/**
 * @author shizhishi
 */
public class BlockServiceImpl implements BlockService {

    @Override
    public String getBalance() {
        JSONObject json = new JSONObject();
        String balance = XchainConfig.client.getBalance(XchainConfig.account.getContractAccount()).toString();
        System.out.println("---获取余额---");
        System.out.println("余额：" + balance);
        json.put("balance",balance);
        return json.toJSONString();
    }
}
