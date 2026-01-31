INSERT INTO storage.product (maker, model, type) VALUES
('Acer', 'AC100', 'PC'),
('Acer', 'AC200', 'Laptop'),
('Acer', 'AC300', 'Printer'),
('HP', 'HP550', 'PC'),
('HP', 'HP660', 'Laptop'),
('HP', 'HP770', 'Printer'),
('Dell', 'DL800', 'PC'),
('Dell', 'DL900', 'Laptop'),
('Lenovo', 'LN450', 'Laptop');

INSERT INTO storage.pc (code, model, speed, ram, hd, cd, price) VALUES
(1, 'AC100', 3200, 16, 512, '8x', 45000),
(2, 'HP550', 3600, 32, 1024, '16x', 65000),
(3, 'DL800', 3400, 16, 1000, '12x', 55000),
(4, 'HP550', 3800, 64, 2048, '24x', 85000);  -- Вторая модель HP550 с другими характеристиками

INSERT INTO storage.laptop (code, model, speed, ram, hd, screen, price) VALUES
(101, 'AC200', 2700, 8, 256, 15, 35000),
(102, 'HP660', 2900, 16, 512, 14, 55000),
(103, 'DL900', 3100, 32, 1024, 17, 75000),
(104, 'LN450', 2800, 16, 512, 15, 48000);

INSERT INTO storage.printer (code, model, color, type, price) VALUES
(201, 'AC300', 'y', 'Laser', 12000),
(202, 'HP770', 'n', 'Jet', 8000),
(203, 'AC300', 'n', 'Laser', 10000),  -- Вторая модель AC300 черно-белая
(204, 'HP770', 'y', 'Matrix', 15000);