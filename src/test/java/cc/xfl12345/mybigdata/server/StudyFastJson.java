package cc.xfl12345.mybigdata.server;

import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;

public class StudyFastJson {

    @JSONField()
    public Long tt = 666L;

    public static void main(String[] args) {
        JSONObject jsonObject = JSONObject.parseObject(
            JSON.toJSONString(new GlobalDataRecord(), JSONWriter.Feature.WriteMapNullValue),
            JSONObject.class
        );
        // JSON.toJavaObject()
        System.out.println(jsonObject.keySet());

    }
}
