package realmtest.bean;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author 陈晓辉
 * @description <>
 * @date 17/6/6
 */

public class Toy extends RealmObject implements IPrinter {
    @PrimaryKey
    public int id;

    @Override
    public String print(int position) {
        return String.format(Locale.getDefault(), "Toy:%1$d,listIndex:%2$d", id, position);
    }

    public static void add(Realm realm) {
        IdCreator state = realm.where(IdCreator.class).findFirst();
        if (null == state) {
            state = realm.createObject(IdCreator.class);
        }
        state.toyId = state.toyId + 1;
        realm.createObject(Toy.class, state.toyId);
    }
}
