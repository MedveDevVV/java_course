package autoservice.repository.impl;

import autoservice.model.WorkshopPlace;
import autoservice.repository.WorkshopPlaceRepository;

import java.util.List;
import java.util.Optional;

public class JpaWorkshopPlaceRepository extends AbstractJpaRepository implements WorkshopPlaceRepository {

    @Override
    public void addPlace(WorkshopPlace place) {
        executeInTransaction(em -> em.persist(place));
        logger.info("Добавлено рабочее место: {}", place.getId());
    }

    @Override
    public void removePlace(WorkshopPlace place) {
        executeInTransaction(em -> {
            em.createQuery("delete from WorkshopPlace wp where wp.id = :id")
                    .setParameter("id", place.getId())
                    .executeUpdate();
        });
        logger.info("Удалено рабочее место: {}", place.getId());
    }

    @Override
    public List<WorkshopPlace> getAllPlaces() {
        return executeWithResult(em ->
                em.createQuery("SELECT wp FROM WorkshopPlace wp ORDER BY wp.name", WorkshopPlace.class)
                        .getResultList()
        );
    }

    @Override
    public WorkshopPlace findByName(String name) {
        Optional<WorkshopPlace> result = executeWithResult(em ->
                em.createQuery("SELECT wp FROM WorkshopPlace wp WHERE wp.name = :name", WorkshopPlace.class)
                        .setParameter("name", name)
                        .getResultStream()
                        .findFirst()
        );
        return result.orElse(null);
    }
}