package autoservice.service;

import autoservice.repository.RepositoryFactory;

public abstract class AutoServiceAdminFactory {
    public static AutoServiceAdmin createService(RepositoryFactory repositoryFactory) {
        return new AutoServiceAdmin(
                repositoryFactory.createMasterRepository(),
                repositoryFactory.createWorkshopPlaceRepository(),
                repositoryFactory.createOrderRepository()
        );
    }
}