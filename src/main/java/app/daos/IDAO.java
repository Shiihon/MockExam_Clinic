package app.daos;

import java.util.Set;

public interface IDAO<T, J> {

    Set<T> getAll();

    T getById(Long id);

    Set<T> getBySpeciality(J j);

    T create(T t);

    T update(Long id, T t);
}

