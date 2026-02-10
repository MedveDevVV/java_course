package autoservice.dao;

import autoservice.database.ConnectionManager;
import autoservice.dto.CarServiceMasterFilter;
import autoservice.exception.DaoException;
import autoservice.exception.EntityInUseException;
import autoservice.model.CarServiceMaster;
import org.postgresql.util.PSQLException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public final class CarServiceMasterDAO implements GenericDAO<UUID, CarServiceMaster, CarServiceMasterFilter> {
    private static final CarServiceMasterDAO INSTANCE = new CarServiceMasterDAO();

    private static final String SAVE_SQL = """
            INSERT INTO service.car_service_masters (full_name, date_of_birth) VALUES (?,?) """;
    private static final String GET_ALL_SQL = "SELECT id, full_name, date_of_birth FROM service.car_service_masters";
    private static final String GET_BY_FULL_NAME_SQL = GET_ALL_SQL + " where full_name = ?";
    private static final String GET_BY_ID_SQL = GET_ALL_SQL + " where id = ?";
    private static final String DELETE_SQL = "DELETE FROM service.car_service_masters WHERE id = ?";
    private static final String UPDATE_SQL = "UPDATE service.car_service_masters SET full_name = ?, date_of_birth = ? WHERE id = ?";

    private CarServiceMasterDAO() {
    }

    public static CarServiceMasterDAO getINSTANCE() {
        return INSTANCE;
    }

    private CarServiceMaster getMaster(ResultSet resultSet) throws SQLException {
        return new CarServiceMaster(
                resultSet.getObject("id", UUID.class),
                resultSet.getString("full_name"),
                resultSet.getDate("date_of_birth").toLocalDate());
    }

    private void setStatementParameters(CarServiceMaster master, PreparedStatement statement) throws SQLException {
        statement.setString(1, master.getFullName());
        statement.setDate(2, java.sql.Date.valueOf(master.getDateOfBirth()));
    }

    @Override
    public CarServiceMaster save(CarServiceMaster master) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setStatementParameters(master, statement);
            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            if (keys.next()) {
                master.setId(keys.getObject("id", UUID.class));
            }
            return master;
        } catch (SQLException e) {
            throw new DaoException("Ошибка сохранения мастера: " + master.getFullName(), e);
        }
    }

    @Override
    public List<CarServiceMaster> findAll() {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(GET_ALL_SQL)) {
            List<CarServiceMaster> masters = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                masters.add(getMaster(resultSet));
            }
            return masters;
        } catch (SQLException e) {
            throw new DaoException("Ошибка получения списка мастеров", e);
        }
    }

    @Override
    public List<CarServiceMaster> findAll(CarServiceMasterFilter filter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();
        if (filter.name() != null) {
            parameters.add("%" + filter.name() + "%");
            whereSql.add(" full_name ILIKE ?");
        }
        if (filter.dateOfBirth() != null) {
            parameters.add(filter.dateOfBirth());
            whereSql.add(" date_of_birth = ?");
        }
        parameters.add(filter.limit());
        parameters.add(filter.offset());
        String sql = GET_ALL_SQL + whereSql.stream().collect(Collectors.joining(
                whereSql.size() > 1 ? " AND " : " ",
                whereSql.isEmpty() ? " " : " WHERE ",
                " LIMIT ? OFFSET ?"
        ));

        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.size(); ++i) {
                statement.setObject(i + 1, parameters.get(i));
            }
            ResultSet resultSet = statement.executeQuery();
            List<CarServiceMaster> masters = new ArrayList<>();
            while (resultSet.next()) {
                masters.add(getMaster(resultSet));
            }
            return masters;
        } catch (SQLException e) {
            throw new DaoException("Ошибка фильтрации мастеров", e);
        }
    }

    @Override
    public Optional<CarServiceMaster> findById(UUID id) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(GET_BY_ID_SQL)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getMaster(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DaoException("Ошибка поиска мастера по ID: " + id, e);
        }
    }

    @Override
    public boolean update(CarServiceMaster master) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(UPDATE_SQL)) {
            setStatementParameters(master, statement);
            statement.setObject(3, master.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Ошибка обновления мастера: " + master.getId(), e);
        }
    }

    @Override
    public boolean delete(CarServiceMaster master) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setObject(1, master.getId());
            return statement.executeUpdate() > 0;
        } catch (PSQLException e) {
            throw new EntityInUseException("Не возможно удалить мастера назначенного на заказ");
        } catch (SQLException e) {
            throw new DaoException("Ошибка удаления мастера: " + master.getId(), e);
        }
    }

    public Optional<CarServiceMaster> findByFullName(String fullName) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(GET_BY_FULL_NAME_SQL)) {
            statement.setString(1, fullName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getMaster(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DaoException("Ошибка поиска мастера по имени: " + fullName, e);
        }
    }
}
