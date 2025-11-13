package autoservice.repository.impl;

import autoservice.repository.MasterRepository;
import autoservice.repository.OrderRepository;
import autoservice.repository.RepositoryFactory;
import autoservice.repository.WorkshopPlaceRepository;

public class GarageRepositoryFactory implements RepositoryFactory {
    @Override
    public MasterRepository createMasterRepository() {
        return new GarageMasterRepository();
    }

    @Override
    public WorkshopPlaceRepository createWorkshopPlaceRepository() {
        return new GarageWorkshopPlaceRepository();
    }

    @Override
    public OrderRepository createOrderRepository() {
        return new RepairOrderRepository();
    }
}