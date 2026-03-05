ALTER TABLE service.repair_orders
    ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN service.repair_orders.deleted IS 'Флаг мягкого удаления заказа';