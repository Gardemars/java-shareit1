CREATE TYPE IF NOT EXISTS BOOKING_STATUS AS ENUM ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED');

CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT AUTO_INCREMENT,
    email   VARCHAR(50) UNIQUE NOT NULL,
    name    VARCHAR(50)        NOT NULL,
    constraint USERS_PK
        primary key (user_id)
);

CREATE TABLE IF NOT EXISTS items
(
    item_id     BIGINT AUTO_INCREMENT,
    name        VARCHAR(50)  NOT NULL,
    description VARCHAR(255) NOT NULL,
    available   BOOLEAN      NOT NULL,
    owner_id    INTEGER      NOT NULL,
    constraint ITEMS_PK
        primary key (item_id),
    constraint "ITEMS_USERS_fk"
        foreign key (owner_id) references users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS booking
(
    booking_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_time TIMESTAMP NOT NULL,
    end_time   TIMESTAMP NOT NULL,
    item_id    INTEGER   NOT NULL,
    booker_id  INTEGER   NOT NULL,
    status     BOOKING_STATUS DEFAULT 'WAITING',
    constraint BOOKING_PK
        primary key (booking_id),
    constraint "BOOKING_USERS_fk"
        foreign key (booker_id) references users (user_id) ON DELETE CASCADE,
    constraint "BOOKING_ITEMS_fk"
        foreign key (item_id) references items ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id BIGINT AUTO_INCREMENT,
    text       VARCHAR(255) NOT NULL,
    item_id    INTEGER      NOT NULL,
    author_id  INTEGER      NOT NULL,
    created    TIMESTAMP    NOT NULL,
    constraint COMMENTS_PK
        primary key (comment_id),
    constraint "COMMENTS_ITEMS_fk"
        foreign key (item_id) references items ON DELETE CASCADE,
    constraint "COMMENTS_USERS_fk"
        foreign key (author_id) references users (user_id) ON DELETE CASCADE
);