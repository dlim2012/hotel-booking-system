


docker exec -u root -it hb-cassandra cqlsh -u cassandra -p cassandra -e "
    CREATE KEYSPACE mykeyspace WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1} AND DURABLE_WRITES = true;
    USE mykeyspace;
    CREATE TYPE booking_archive_room(
      roomsId INT,
      roomId BIGINT,
      roomsName TEXT,
      startDateTime TIMESTAMP,
      endDateTime TIMESTAMP,
      guestName TEXT,
      guestEmail TEXT
    );
    CREATE TABLE booking_archive_by_user_id (
      user_id INT,
      main_status TEXT,
      end_date_time TIMESTAMP,
      hotel_id INT,
      booking_id BIGINT,
      hotel_manager_id INT,
      reservation_time TIMESTAMP,
      first_name TEXT,
      last_name TEXT,
      email TEXT,
      hotel_name TEXT,
      neighborhood TEXT,
      city TEXT,
      state TEXT,
      country TEXT,
      rooms list<frozen<booking_archive_room>>,
      status TEXT,
      start_date_time TIMESTAMP,
      price_in_cents BIGINT,
      invoice_id TEXT,
      invoice_confirm_time TIMESTAMP,
      PRIMARY KEY ((user_id), main_status, end_date_time)
    ) WITH CLUSTERING ORDER BY (main_status DESC, end_date_time DESC);
    CREATE TABLE booking_archive_by_hotel_id (
      hotel_id INT,
      main_status TEXT,
      end_date_time TIMESTAMP,
      user_id INT,
      booking_id BIGINT,
      hotel_manager_id INT,
      reservation_time TIMESTAMP,
      first_name TEXT,
      last_name TEXT,
      email TEXT,
      hotel_name TEXT,
      neighborhood TEXT,
      city TEXT,
      state TEXT,
      country TEXT,
      rooms list<frozen<booking_archive_room>>,
      status TEXT,
      start_date_time TIMESTAMP,
      price_in_cents BIGINT,
      invoice_id TEXT,
      invoice_confirm_time TIMESTAMP,
      PRIMARY KEY ((hotel_id), main_status, end_date_time)
    ) WITH CLUSTERING ORDER BY (main_status DESC, end_date_time DESC)
    "

docker exec -it hb-redis redis-cli config set notify-keyspace-events Ex;


docker restart hb-booking-management
docker restart hb-archival
docker restart hb-search

