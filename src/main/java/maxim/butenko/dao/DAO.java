package maxim.butenko.dao;

import java.sql.SQLException;
import java.util.List;

public interface DAO<T> {

    List<T> findAll() throws SQLException;

    T update(T t) throws SQLException;

    T save(T t) throws SQLException;
}