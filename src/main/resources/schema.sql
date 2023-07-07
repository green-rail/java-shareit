CREATE TABLE IF NOT EXISTS users (
  user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (user_id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
  item_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  sharer_id BIGINT NOT NULL,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(1024) NOT NULL,
  available BOOLEAN,
  request_id BIGINT,
  CONSTRAINT pk_item PRIMARY KEY (item_id),
  CONSTRAINT fk_item FOREIGN KEY (sharer_id) REFERENCES PUBLIC.users(user_id) ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS bookings (
  booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_date timestamp,
  end_date timestamp,
  item_id BIGINT NOT NULL,
  booker_id BIGINT NOT NULL,
  status VARCHAR(50) NOT NULL,
  CONSTRAINT pk_booking PRIMARY KEY (booking_id),
  CONSTRAINT fk_booking FOREIGN KEY (item_id) REFERENCES PUBLIC.items(item_id) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT fk_booking_1 FOREIGN KEY (booker_id) REFERENCES PUBLIC.users(user_id) ON DELETE CASCADE ON UPDATE RESTRICT
);
