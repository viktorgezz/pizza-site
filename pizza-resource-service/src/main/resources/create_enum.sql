DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'role') THEN
        CREATE TYPE role AS ENUM ('USER', 'MANAGER', 'WORKER', 'MAIN');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'status_restaurant') THEN
        CREATE TYPE status_restaurant AS ENUM ('OPEN', 'CLOSED');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'status_courier') THEN
        CREATE TYPE status_courier AS ENUM ('FREE', 'BUSY');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'status_order') THEN
        CREATE TYPE status_order AS ENUM ('PENDING', 'CONFIRMED', 'DELIVERED', 'CANCELLED');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'type_order') THEN
        CREATE TYPE type_order AS ENUM ('DELIVERY', 'PICKUP');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'measure') THEN
        CREATE TYPE measure AS ENUM ('KG', 'LITERS', 'UNIT');
    END IF;
END$$;