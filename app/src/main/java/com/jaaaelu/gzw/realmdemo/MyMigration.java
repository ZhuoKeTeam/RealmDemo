package com.jaaaelu.gzw.realmdemo;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by admin on 2016/12/1. 当我们更新数据库字段时会使用这个类
 */
public class MyMigration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        //  比如当老的版本为3的时候
        if (oldVersion == 3) {
            schema.get("TestModel")
                    //  添加新的Realm支持的字段
                    .addField("testBassType", int.class)
                    //  添加新的自定义对象
                    .addRealmObjectField("testRealmObject", schema.get("OtherTestModel"))
                    //  添加新的List对象，用于一对多 多对多
                    .addRealmListField("testRealmList", schema.get("OtherTestModel"));
            oldVersion++;
        }
    }
}
