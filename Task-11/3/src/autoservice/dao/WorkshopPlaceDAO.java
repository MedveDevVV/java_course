package autoservice.dao;

import autoservice.database.ConnectionManager;
import autoservice.dto.WorkshopPlaceFilter;
import autoservice.model.WorkshopPlace;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class WorkshopPlaceDAO implements GenericDAO<UUID, WorkshopPlace, WorkshopPlaceFilter> {
    private final static WorkshopPlaceDAO INSTANCE = new WorkshopPlaceDAO();

    private final static String SAVE_SQL = "INSERT INTO service.workshop_places(name) VALUES (?)";
    private final static String GET_ALL_SQL = "SELECT id, name FROM service.workshop_places";
    private final static String GET_BY_PLACE_NAME_SQL = GET_ALL_SQL + " WHERE name = ?";
    private final static String GET_BY_ID_SQL = GET_ALL_SQL + " WHERE id = ?";
    private final static String UPDATE_SQL = "UPDATE service.workshop_places SET name = ? WHERE id = ?";
    private final static String DELETE_SQL = "DELETE FROM service.workshop_places WHERE id = ?";

    private WorkshopPlaceDAO() {
    }

    private static WorkshopPlace getPlace(ResultSet resultSet) throws SQLException {
        return new WorkshopPlace((UUID) resultSet.getObject("id", UUID.class),
                resultSet.getString("name"));
    }

    public static WorkshopPlaceDAO getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public WorkshopPlace save(WorkshopPlace place) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, place.getName());
            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            if(keys.next()){
                place.setId(keys.getObject("id", UUID.class));
            }
            return place;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<WorkshopPlace> findAll() {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(GET_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            List<WorkshopPlace> workshopPlaces = new ArrayList<>();
            while (resultSet.next()) {
                workshopPlaces.add(getPlace(resultSet));
            }
            return workshopPlaces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<WorkshopPlace> findAll(WorkshopPlaceFilter filter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();
        if (filter.name() != null) {
            parameters.add("%" + filter.name() + "%");
            whereSql.add(" name ILIKE ?");
        }
        parameters.add(filter.limit());
        parameters.add(filter.offset());
        String sql = GET_ALL_SQL + whereSql.stream().collect(Collectors.joining(
                " ", whereSql.isEmpty() ? " " : " WHERE ", "LIMIT ? OFFSET ?"));

        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.size(); ++i) {
                statement.setObject(i + 1, parameters.get(i));
            }
            ResultSet resultSet = statement.executeQuery();
            List<WorkshopPlace> places = new ArrayList<>();
            while (resultSet.next()) {
                places.add(getPlace(resultSet));
            }
            return places;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<WorkshopPlace> findById(UUID id) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(GET_BY_ID_SQL)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getPlace(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<WorkshopPlace> findByName(String name) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(GET_BY_PLACE_NAME_SQL)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return Optional.of(getPlace(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(WorkshopPlace place) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, place.getName());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(WorkshopPlace place) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setObject(1, place.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
