


docker exec -u root -it hotel-booking-cassandra cqlsh -u cassandra -p cassandra -e "
    CREATE KEYSPACE mykeyspace WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1} AND DURABLE_WRITES = true;
    USE mykeyspace;
    CREATE TYPE booking_archive_rooms(
      roomsId INT
    );
    CREATE TABLE booking_archive(
      booking_id BIGINT,
      user_id INT,
      hotel_id INT,
      rooms list<frozen<booking_archive_rooms>>,
      start_date_time TIMESTAMP,
      end_date_time TIMESTAMP,
      status TEXT,
      price_in_cents BIGINT,
      invoice_id TEXT,
      invoice_confirm_time TIMESTAMP,
      PRIMARY KEY ((booking_id))
    );
    CREATE TABLE booking_archive_by_user_id (
      user_id INT,
      main_status TEXT,
      end_date_time TIMESTAMP,
      booking_id BIGINT,
      PRIMARY KEY ((user_id), main_status, end_date_time, booking_id)
    ) WITH CLUSTERING ORDER BY (main_status DESC, end_date_time DESC);
    CREATE TABLE booking_archive_by_hotel_id (
      hotel_id INT,
      main_status TEXT,
      end_date_time TIMESTAMP,
      booking_id BIGINT,
      PRIMARY KEY ((hotel_id), main_status, end_date_time, booking_id)
    ) WITH CLUSTERING ORDER BY (main_status DESC, end_date_time DESC)
    "

docker exec -it hotel-booking-redis-server redis-cli config set notify-keyspace-events Ex;

