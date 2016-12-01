package com.jaaaelu.gzw.realmdemo;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by admin on 2016/11/28.通过继承实现
 */

public class TestModel extends RealmObject{
    @PrimaryKey
    private String _id;
    private String testTitle;
    private String updateTime;

    //  用于测试数据库字段变更 版本变成是的时候放开下面的字段
//    public int testBassType;
//    public OtherTestModel testRealmObject;
//    public RealmList<OtherTestModel> testRealmList;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

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
