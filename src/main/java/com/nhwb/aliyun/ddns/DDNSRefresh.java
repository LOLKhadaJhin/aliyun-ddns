package com.nhwb.aliyun.ddns;

import com.aliyun.alidns20150109.Client;
import com.aliyun.alidns20150109.models.*;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DDNSRefresh {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Client client;
    @Value("${dns.client.domainName}")
    private String domainName;
    @Value("${dns.client.all}")
    private boolean all;
    private String oldIP;
    private String url = "http://4.ipw.cn/";
    private String recordId;
    private final Map<String, String> recordIds = new HashMap<>();

    //1获取最新的ip
    public String getNewIP() {
        //获取实体ResponseEntity，可以用get函数获取相关信息
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            final String body = responseEntity.getBody();
            if (body != null && body.length() > 5) {
                return body;
            }
        } catch (ResourceAccessException ignored) {
        }
        try {
            url = "http://6.ipw.cn/";
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            final String body = responseEntity.getBody();
            if (body != null && body.length() > 5) {
                return body;
            }
        } catch (ResourceAccessException ignored) {
        }
        return null;
    }

    //2 获取当前ip,第一次会更新recordIds
    public String getOldIP() {
        if (oldIP == null) {
            if (getRecord() != null) {
                final String newIP = getNewIP();
                for (DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord r : getRecord()) {
                    recordIds.put(r.getRecordId(), r.getRR());
                    if (!r.getValue().equals(newIP)) {
                        updateDomainRecordRequest(r.getRecordId(), r.getRR(), newIP);
                    }
                    if (r.getRR().equals("www")) {
                        recordId = r.getRecordId();
                        oldIP = r.getValue();
                    }
                }
            }
        }
        if (oldIP == null) {
            addDomainRecordRequest(getNewIP());
        }
        return oldIP;
    }

    //3 对比IP，如果不一致则修改
    public void refresh() {
        final String newIP = getNewIP();
        if (newIP == null) {
            System.out.println("你没有公网或者获取公网IP失败");
        } else if (getOldIP() == null) {
            System.out.println("配置出问题了");
        } else if (!getOldIP().equals(newIP)) {
            oldIP = newIP;
            //修改阿里云信息
            if (all) {
                allUpdateDomainRecordRequest(newIP);
            } else {
                oneUpdateDomainRecordRequest(newIP);
            }
        }
    }

    public void setOldIPIsNull(){
        oldIP = null;
    }
    //查看
    private List<DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord> getRecord() {
        DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest()
                .setDomainName(domainName);
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            final DescribeDomainRecordsResponse describeDomainRecordsResponse = client.describeDomainRecordsWithOptions(describeDomainRecordsRequest, runtime);
            final List<DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord> record = describeDomainRecordsResponse.getBody().getDomainRecords().getRecord();
            if (record != null && record.size() > 0) {
                return record;
            }
        } catch (TeaException error) {
            // 如有需要，请打印 error
            System.out.println(Common.assertAsString(error.message));
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            System.out.println(Common.assertAsString(error.message));
        }
        return null;
    }

    //添加解析
    private void addDomainRecordRequest(String newIP) {
        AddDomainRecordRequest addDomainRecordRequest = new AddDomainRecordRequest()
                .setDomainName(domainName)
                .setRR("www")
                .setType("A")
                .setValue(newIP);
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            final AddDomainRecordResponse addDomainRecordResponse = client.addDomainRecordWithOptions(addDomainRecordRequest, runtime);
            recordIds.put(addDomainRecordResponse.body.getRecordId(), "www");
            oldIP = newIP;
        } catch (TeaException error) {
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
    }

    //修改解析
    private void updateDomainRecordRequest(String recordId, String rr, String newIP) {
        UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest()
                .setRecordId(recordId)
                .setRR(rr)
                .setType("A")
                .setValue(newIP);
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            client.updateDomainRecordWithOptions(updateDomainRecordRequest, runtime);
        } catch (TeaException error) {
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
    }

    private void oneUpdateDomainRecordRequest(String newIP) {
        updateDomainRecordRequest(recordId, "www", newIP);
    }

    private void allUpdateDomainRecordRequest(String newIP) {
        recordIds.forEach((k, v) -> updateDomainRecordRequest(k, v, newIP));
    }
}
