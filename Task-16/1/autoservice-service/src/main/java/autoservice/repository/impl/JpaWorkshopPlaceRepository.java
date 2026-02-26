package autoservice.repository.impl;

import autoservice.model.WorkshopPlace;
import autoservice.repository.WorkshopPlaceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class JpaWorkshopPlaceRepository extends AbstractJpaRepository implements WorkshopPlaceRepository {

    @Override
    public void addPlace(WorkshopPlace place) {
        execute("Добавление рабочего места",
                em -> em.persist(place),
                place.getName());
    }

    @Override
    public void removePlace(WorkshopPlace place) {
        execute("Удаление рабочего места",
                em -> {
                    em.createQuery("delete from WorkshopPlace wp where wp.id = :id")
                            .setParameter("id", place.getId())
                            .executeUpdate();
                },
                place.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkshopPlace> getAllPlaces() {
        return executeWithResult("Получение всех рабочих мест",
                em ->
                em.createQuery("SELECT wp FROM WorkshopPlace wp ORDER BY wp.name", WorkshopPlace.class)
                        .getResultList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WorkshopPlace> findByName(String name) {
        return executeWithResult("Поиск рабочего места по имени",
                em ->
                em.createQuery("SELECT wp FROM WorkshopPlace wp WHERE wp.name = :name", WorkshopPlace.class)
                        .setParameter("name", name)
                        .getResultStream()
                        .findFirst(),
                name
        );
    }

    @Override
    public Optional<WorkshopPlace> findById(UUID id) {
        return executeWithResult("Поиск рабочего места по ID",
                em ->
                Optional.ofNullable(em.find(WorkshopPlace.class, id)),
                id
        );
    }

    @Override
    public List<WorkshopPlace> findByNameContaining(String name) {
        return executeWithResult("Поиск рабочих мест по части имени",
                em ->
                em.createQuery("SELECT wp FROM WorkshopPlace wp WHERE wp.name ILIKE :name", WorkshopPlace.class)
                        .setParameter("name", "%" + name + "%")
                        .getResultList(),
                name
        );
    }
}