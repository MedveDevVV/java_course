package autoservice.dao;

import autoservice.database.ConnectionManager;
import autoservice.dto.RepairOrderFilter;
import autoservice.enums.OrderStatus;
import autoservice.exception.DaoException;
import autoservice.model.CarServiceMaster;
import autoservice.model.RepairOrder;
import autoservice.model.WorkshopPlace;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class RepairOrderDAO implements GenericDAO<UUID, RepairOrder, RepairOrderFilter> {
    private static final RepairOrderDAO INSTANCE = new RepairOrderDAO();

    private final static String SAVE_SQL = """
            INSERT INTO service.repair_orders 
                (description, creation_date, start_date, end_date, status, total_price, master_id, place_id)
            VALUES (?,?,?,?,?,?,?,?)""";
    private final static String GET_ALL_SQL = """
            SELECT ro.id, ro.description, ro.creation_date, ro.start_date, ro.end_date, ro.status, ro.total_price,
                   ro.master_id, ro.place_id,
                   csm.full_name, csm.date_of_birth, wp.name
            FROM service.repair_orders AS ro
            LEFT JOIN service.car_service_masters AS csm on ro.master_id = csm.id
            LEFT JOIN service.workshop_places AS wp on ro.place_id = wp.id""";
    private final static String GET_BY_ID_SQL = GET_ALL_SQL + " WHERE ro.id = ?";
    private final static String UPDATE_SQL = """
            UPDATE service.repair_orders 
            SET description = ?, creation_date = ?, start_date = ?, end_date = ?, 
                status = ?, total_price = ?, master_id = ?, place_id = ? 
            WHERE id = ?""";
    private final static String DELETE_SQL = "DELETE FROM service.repair_orders WHERE id = ?";

    private RepairOrderDAO() {
    }

    private RepairOrder getOrder(ResultSet resultSet) throws SQLException {
        UUID masterId = (UUID) resultSet.getObject("master_id", UUID.class);
        CarServiceMaster master = masterId == null ? null :
                new CarServiceMaster(masterId, resultSet.getString("full_name"),
                        resultSet.getDate("date_of_birth").toLocalDate());
        UUID placeId = (UUID) resultSet.getObject("place_id", UUID.class);
        WorkshopPlace place = placeId == null ? null :
                new WorkshopPlace(placeId, resultSet.getString("name"));
        return new RepairOrder(
                resultSet.getDate("creation_date").toLocalDate(),
                resultSet.getDate("start_date").toLocalDate(),
                resultSet.getDate("end_date").toLocalDate(),
                resultSet.getString("description"),
                OrderStatus.valueOf(resultSet.getString("status")),
                resultSet.getFloat("total_price"),
                (UUID) resultSet.getObject("id"),
                master,
                place);
    }

    private void setStatementParameters(RepairOrder order, PreparedStatement statement) throws SQLException {
        statement.setString(1, order.getDescription());
        statement.setObject(2, java.sql.Date.valueOf(order.getCreationDate()));
        statement.setObject(3, java.sql.Date.valueOf(order.getStartDate()));
        statement.setObject(4, java.sql.Date.valueOf(order.getEndDate()));
        statement.setObject(5, order.getStatus(), Types.OTHER);
        statement.setFloat(6, order.getTotalPrice() != null ? order.getTotalPrice() : 0.0f);
        statement.setObject(7, order.getCarServiceMasterId());
        statement.setObject(8, order.getPlaceId());
    }

    public static RepairOrderDAO getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public RepairOrder save(RepairOrder order) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setStatementParameters(order, statement);
            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            if (keys.next()) {
                order.setId(keys.getObject("id", UUID.class));
            }
            return order;
        } catch (SQLException e) {
            throw new DaoException("Ошибка сохранения заказа", e);
        }
    }

    @Override
    public List<RepairOrder> findAll() {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(GET_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            List<RepairOrder> orders = new ArrayList<>();
            while (resultSet.next()) {
                orders.add(getOrder(resultSet));
            }
            return orders;
        } catch (SQLException e) {
            throw new DaoException("Ошибка получения списка заказов", e);
        }
    }

    @Override
    public List<RepairOrder> findAll(RepairOrderFilter filter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();
        if (filter.status() != null) {
            parameters.add(filter.status());
            whereSql.add(" status = ?::service.order_status");
        }
        if (filter.place() != null) {
            parameters.add(filter.place().getId());
            whereSql.add(" place_id = ?");
        }
        if (filter.creationDate() != null) {
            parameters.add(filter.creationDate());
            whereSql.add(" creation_date = ?");
        }
        if (filter.endDate() != null) {
            parameters.add(filter.endDate());
            whereSql.add(" end_date = ?");
        }
        if (filter.startDate() != null) {
            parameters.add(filter.startDate());
            whereSql.add(" start_date = ?");
        }
        if (filter.carServiceMaster() != null) {
            parameters.add(filter.carServiceMaster().getId());
            whereSql.add(" master_id = ?");
        }
        if (filter.description() != null) {
            parameters.add("%" + filter.description() + "%");
            whereSql.add(" description ILIKE ?");
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
            List<RepairOrder> orders = new ArrayList<>();
            while (resultSet.next()) {
                orders.add(getOrder(resultSet));
            }
            return orders;
        } catch (SQLException e) {
            throw new DaoException("Ошибка фильтрации заказов", e);
        }

    }

    @Override
    public Optional<RepairOrder> findById(UUID id) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(GET_BY_ID_SQL)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getOrder(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DaoException("Ошибка поиска заказа по ID: " + id, e);
        }
    }

    @Override
    public boolean update(RepairOrder order) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(UPDATE_SQL)) {
            setStatementParameters(order, statement);
            statement.setObject(9, order.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Ошибка обновления заказа: " + order.getId(), e);
        }
    }

    @Override
    public boolean delete(RepairOrder order) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setObject(1, order.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Ошибка удаления заказа: " + order.getId(), e);
        }
    }

    public boolean updateOrdersInTransaction(List<RepairOrder> orders) {
        Connection connection = null;
        try {
            connection = ConnectionManager.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
                for (RepairOrder order : orders) {
                    setStatementParameters(order, statement);
                    statement.setObject(9, order.getId());
                    statement.addBatch();
                }

                int[] results = statement.executeBatch();
                for (int result : results) {
                    if (result <= 0) {
                        connection.rollback(); // Откатываем, если не все обновились
                        return false;
                    }
                }
                connection.commit();
                return true;
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback(); // Откат при исключении
                } catch (SQLException rollbackEx) {
                    e.addSuppressed(rollbackEx);
                }
            }
            throw new DaoException("Ошибка при пакетном обновлении заказов", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("КРИТИЧЕСКАЯ ОШИБКА: Не удалось вернуть соединение в пул: " + e.getMessage());
                }
            }
        }
    }
}
