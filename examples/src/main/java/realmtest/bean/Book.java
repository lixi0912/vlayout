package realmtest.bean;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author 陈晓辉
 * @description <>
 * @date 17/3/27
 */

public class Book extends RealmObject implements IPrinter {

    @PrimaryKey
    public int id;
    public int index;

    @Override
    public String print(int position) {
        return String.format(Locale.getDefault(), "Book:%1$d,listIndex:%2$d", id, position);
    }

    public static void add(Realm realm) {
        IdCreator state = realm.where(IdCreator.class).findFirst();
        if (null == state) {
            state = realm.createObject(IdCreator.class);
        }
        state.bookId = state.bookId + 1;
        Book book = realm.createObject(Book.class, state.bookId);
        book.index = book.id;
    }
}
