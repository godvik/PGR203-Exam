package no.kristiania.dao;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao<DaoType> {

    private final DataSource dataSource;

    public AbstractDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long insert(DaoType obj, String query, String column) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                insertIntoDatabase(obj, preparedStatement);
                preparedStatement.executeUpdate();

                ResultSet rs = preparedStatement.getGeneratedKeys();
                rs.next();

                return rs.getLong(column);
            }
        }
    }

    public List<DaoType> list(String query) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<DaoType> list = new ArrayList<>();
                    while (rs.next()) {
                        list.add(readFromResultSet(rs));
                    }
                    return list;
                }
            }
        }
    }

    protected abstract DaoType readFromResultSet(ResultSet rs) throws SQLException;

    protected abstract void insertIntoDatabase(DaoType obj, PreparedStatement statement) throws SQLException;
}
