    DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'role') THEN
            CREATE TYPE role AS ENUM ('USER', 'MANAGER', 'WORKER', 'MAIN');
        END IF;
    END
    $$;