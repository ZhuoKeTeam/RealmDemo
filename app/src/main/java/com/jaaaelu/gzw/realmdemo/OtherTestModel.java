package com.jaaaelu.gzw.realmdemo;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by admin on 2016/11/28.通过接口实现
 */
@RealmClass
public class OtherTestModel implements RealmModel{
    private String testTitle;
    private String updateTime;

    public String getTestTitle() {
        return testTitle;
    }

    public void setTestTitle(String testTitle) {
        this.testTitle = testTitle;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
