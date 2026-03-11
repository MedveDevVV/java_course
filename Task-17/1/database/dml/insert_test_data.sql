SET search_path TO service;

INSERT INTO workshop_places (name) VALUES
	('place_1'),
	('place_2'),
	('place_3'),
	('place_4'),
	('place_5')
;

INSERT INTO car_service_masters (full_name, date_of_birth)
VALUES	('master 1', date '1995-01-01' + ((random() * 1500)::integer) * INTERVAL '1 day'),
	('master 2', date '1995-01-01' + ((random() * 1500)::integer) * INTERVAL '1 day'),
	('master 3', date '1995-01-01' + ((random() * 1500)::integer) * INTERVAL '1 day'),
	('master 4', date '1995-01-01' + ((random() * 1500)::integer) * INTERVAL '1 day'),
	('master 5', date '1995-01-01' + ((random() * 1500)::integer) * INTERVAL '1 day')
;

INSERT INTO repair_orders (description, creation_date, start_date, end_date, status, total_price, master_id, place_id)
VALUES	(
        'Замена масла и фильтров', 
        '2024-01-15', 
        '2024-01-16', 
        '2024-01-17', 
        'CLOSED', 
        5000.00,
        (SELECT id FROM service.car_service_masters LIMIT 1),
        (SELECT id FROM service.workshop_places LIMIT 1)
);