CREATE TABLE IF NOT EXISTS flight_logs (
    flight_number INT,
    departure_airport VARCHAR(50),
    arrival_airport VARCHAR(50),
    departure_date DATE,
    arrival_date DATE,
    departure_time VARCHAR(50),
    arrival_time VARCHAR(50),
    airline VARCHAR(8),
    fare_class VARCHAR(8),
    passenger_count INT
);